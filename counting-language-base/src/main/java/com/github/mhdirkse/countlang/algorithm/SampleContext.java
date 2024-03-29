/*
 * Copyright Martijn Dirkse 2020
 *
 * This file is part of counting-language.
 *
 * counting-language is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.github.mhdirkse.countlang.algorithm;

/**
 * This interface does the calculations behind sampling variables from distributions and
 * scoring values in a result distribution. This way, counting-language statements
 * expressing a probability experiment are evaluated, the result being a
 * {@link Distribution}. A {@link Distribution} is like a multi-set, the count of
 * each element being a non-negative integer. If a value <code>v</code> has count
 * <code>c</code> in the result distribution and if the total count of the result
 * distribution is <code>n</code>, then this encodes that the probability
 * of <code>v</code> is the rational number <code>c / n</code>.
 * <p>
 * Counting-language experiments can also be defined like <code>possibility counting experiment ...</code>
 * For distributions returned from such experiments, the numerators <code>c</code> are
 * meaningful: they can be interpreted as a number of possibilities.
 * <p>
 * This interface has static factory methods that can create two kinds of instances, one
 * kind producing meaningful possibility counts (for experiments like <code>possibility counting experiment ...</code>)
 * and the other kind normalizing to arrive at the lowest possible denominator
 * (for just just <code>experiment ...</code>).
 * <p>
 * The algorithmn implemented here is explained in three steps. First, it is explained
 * how possibility counts are calculated. Second, it is explained how the experiment
 * result is managed. Third, experiments without meaningful possibility counts are
 * treated.
 * 
 * <h2>Counting meaningful possibilities</h2>
 * 
 * As an example, consider a simple game. We have a dice that has two faces with the number
 * 10, three faces with the number 20 and one face with the number 30. And we have a roulette
 * wheel with five equally-sized slots, two of them having the number 10 and the remaining
 * three having the number 20. We throw the dice once. If we got 10 or 20, then we draw the wheel.
 * Otherwise, the wheel is not drawn. 
 * For each combination of (one or two) obtained number, what is the number of possibilities?
 * <p>
 * To clarify the question, lets consider the possible outcome (10, 10). To count the number of
 * possibilities, we can label the dice faces like <code>a</code>, ..., <code>f</code> and the
 * roulette wheel slots <code>p</code>, ..., <code>t</code>. Dice faces <code>a</code> and <code>b</code>
 * have the number 10 and wheel slots <code>p</code> and <code>q</code> have the number 10. The
 * possibilities we have to count are <code>ap</code>, <code>aq</code>, <code>bp</code> and
 * <code>bq</code>, which are 2 * 2 = 4.
 * <p>
 * To count possibilities, we start from two distributions:
 * <ul>
 * <li>2 of 10, 3 of 20, 1 of 30
 * <li>2 of 10, 3 of 20
 * </ul>
 * <p>
 * First, we consider the first distribution, resulting in the probability tree shown below:
 * <p>
 * <img src="./doc-files/countingPossibilities.jpg" alt="Image countingPossibilities.jpg does not show, sorry" >
 * <p>
 * At this point, each counted possibility corresponds to one of the dice faces <code>a</code>, ... <code>f</code>.
 * For each outcome on the faces, the weight corresponds to the number of faces having that outcome.
 * <p>
 * Next, we add the second distribution. The meaning of a possibility changes. One possibility
 * is now the combination of a face <code>a</code>, ... <code>f</code> and a slot
 * <code>p</code>, ..., <code>t</code>. The weight of each node has to be multiplied by 5. The
 * outcome 30 now has five possibilities: we need a specific face but any outcome of
 * the roulette wheel suffices (1 * 5 = 5).
 * <p>
 * We add a second row of nodes for drawing the wheel, as shown in the following figure:
 * <p>
 * <img src="./doc-files/countingPossibilities_2.jpg" alt="Image countingPossibilities_2.jpg does not show, sorry" >
 * <p>
 * Each node at the bottom row shows how many possibilities there are for the path leading to the node. To the
 * bottom-left, we see again that the outcome (10, 10) has 4 possibilities. The weight is calculated by
 * dividing the (new) weight of the parent node by the new distribution's total (resulting in the weight of the previous diagram)
 * and multiplying that with the count of the value within the wheel distribution. These last two operations,
 * dividing by the new distribution's total and multiplying by the new value's count, can be interpreted as multiplying
 * with a conditional probability. This is the conditional probability of drawing a value from the new distribution.
 * <p>
 * Possibility counts are only meaningful for experiments that
 * consist of a series of sub-experiments. The distribution sizes of these sub-experiments
 * may be different, but these sizes may not depend on the sampled values. If this rule would
 * be violated, one would have to work with conditional probabilities that can not be interpreted
 * anymore as possibility counts. The only exception is that sub-experiments may be omitted depending
 * on the values sampled from earlier sub-experiments. The interpretation is then that the omitted
 * sub experiment is done, but that the outcome does not influence the result of the experiment.
 * <p>
 * Please note that the values sampled in a sub-experiment are allowed to depend on the values sampled
 * from earlier sub-experiments. An example is drawing random cards from a set of cards (no layback).
 * Say we draw two cards from five that are labeled 1, ..., 5. If the first drawn card is 1, then the
 * second is drawn from 2, 3, 4 and 5. If the first drawn card is 2, then the second is drawn from
 * 1, 3, 4 and 5. Similar statements hold for the other possibilities for the first card. The values
 * in the second sub-experiment depend on the value sampled from the first, but in any case there
 * are four possibilities in the second sub-experiment.
 * <p>
 * From these examples, we can derive rules for managing the nodes in the probability tree. We
 * start from a series of <code>n</code> sub-experiments <code>S1</code>,..., <code>Sn</code>.
 * These sub-experiments correspond with distributions that depend on the values sampled
 * in earlied sub-experiments, but the total  of each sub-experiment <code>i</code> in
 * 1, ..., n only depends on the index <code>i</code>.
 * <p>
 * The rules are as follows:
 * <ul>
 * <li>Each sub-experiment in the series corresponds to a row in the probability tree.
 * <li>Each node in a row corresponds to a value that can be sampled in the corresponding sub-experiment.
 * <li>Each node in some row <code>i</code> has a weight that expresses the number of possibilities. The counted
 * possibilities apply to sampling the node's value in sub-experiment <code>Si</code> and to sampling the ancestor
 * node values in the preceding sub-experiments.
 * <li>To maintain the correct weights, a new row (sub-experiment) is added as follows. The weights of all existing
 * nodes are multiplied by the total of the new sub-experiments's distribution. The weight of each new node becomes
 * the weight of the parent node divided by the new distribution's total multiplied by the count of the new node's value. 
 * <li>The leaf nodes of the tree are not required to have a uniform distance from the root node. In other words,
 * there may be nodes in some row <code>i &lt; n</code> for which sub-experiments <code>S_(i+1), ..., S_n</code> are
 * not relevant.
 * </ul>
 * 
 * <h2>Scoring results</h2>
 * 
 * Now that we know how possibilities are counted, we can use these counts to produce experiment outcomes.
 * The outcome of an experiment is a distribution, a multiset in which each member has a count.
 * If the experiment has meaningful possibilities in
 * the sense of the previous section, then the count of each member of the outcome distribution is the
 * amount of possibilities to arrive at that member. We need to iterate over all leaf nodes of the probability tree.
 * For each node, we need to score a value, which means that we add that value to the outcome distribution. We
 * increment the count of the value with the count of the probability tree node being visited.
 * <p>
 * As an example, consider the following experiment. We play the game of the previous section and add the drawn
 * numbers. We find the result distribution by iterating over all leaf nodes of the last shown probability tree
 * (see previous section). The lower-left node stands for dice 10 and roulette wheel 10, the combination (10, 10).
 * It produces the result 20 and adds 4 possibilities to arrive at that result. Next comes (10, 20), adding 6
 * possibilities for the result 30. When we continue iterating, we arrive at the following list:
 * <p>
 * <ul>
 * <li>First node: 4 of 20
 * <li>Second node: 6 of 30
 * <li>Third node: 6 of 30
 * <li>Fourth node: 9 of 40
 * <li>Fifth node: 5 of 30
 * </ul>
 * <p>
 * The output follows from adding the different counts for the same outcomes, resulting in:
 * <p>
 * <ul>
 * <li>4 of 20
 * <li>17 of 30
 * <li>9 of 40
 * </ul>
 * <p>
 * Please note that the shown counts add up to 30, 6 possible dice faces times 5 possible roulette wheel slots.
 * <p>
 * Counting-language simultaneously builds the probability tree and scores values into the output distribution.
 * The sequence of iterating over the leaf nodes should not influence the outcome. The first encounted leaf
 * node could also be the dice result 30 without roulette wheel. At this point, the other nodes are unknown,
 * so the roulette wheel is ignored at this point. Only one possibility is scored for the value 30.
 * <p>
 * Next, dice value 20 is encountered. Now the roulette wheel is added as a sub-experiment, resulting in the
 * refinement of the probability tree. We see that the output distribution has to be refined with a factor 5 
 * as well when the probability tree is refined as shown earlier. The single possibility we had for the output 30
 * is multiplied by 5. The first node now adds 5 possibilities for the outcome 30, the same as
 * we had when browsing the leaf nodes from left to right.
 * <p>
 * We have the following rules for managing the output distribution:
 * <p>
 * <ul>
 * <li>The result of an experiment is a distribution, the output distribution. It is calculated by browsing the leaf
 * nodes of the probability tree.
 * <li>For each leaf node, a result value is calculated from the node's value and the ancestor node values. The value's
 * count in the output distribution is incremented with the leaf node's count.
 * <li>The outcome should not depend on the order in which the leaf nodes are browsed. This is achieved by building the
 * output distribution simultaneously with the probability tree. Whenever the probability tree is refined,
 * the output distribution is refined similarly.
 * </ul>
 *
 * <h2>Experiments without meaningful possibility counts</h2>
 *
 * In this section, we drop the restriction of the first section on the sizes
 * of the sub-experiments. As an example, we might use different roulette wheels
 * after different dice outcomes. The wheels are allowed to have a different number of slots.
 * We have to accept that we cannot count possibilities anymore.
 * We only keep probabilities. When a node in the probability tree
 * has an integer weight <code>w</code> and if the weights of the first row
 * add up to <code>n</code>, then this means that the probability of arriving
 * at the node is the rational number <code>w/n</code>. The same applies to
 * the output distribution. When a value <code>v</code> in the output distribution
 * has count <code>c</code>, then the probability of the outcome <code>v</code> is <code>c/n</code>.
 * The total of the output distribution is the same as the size <code>n</code> of the
 * probability tree.
 * <p>
 * We present an example of building a probability tree without meaningful possibility counts. The
 * figure below follows from sampling a first variable:
 * <p>
 * <img src="./doc-files/samplingContext_1.jpg" alt="Image samplingContext_1.jpg does not show, sorry" >
 * <p>
 * Let's call the variable <code>x</code>. This sample variable comes from a distribution with three values,
 * say <code>v11</code>, <code>v12</code> and <code>v13</code>, having
 * counts 1, 1 and 2. The total of this sampled distribution is 4, so the probabilities
 * of the values are 1/4, 1/4 and 2/4.
 * <p>
 * Now, another variable <code>y1</code> is sampled while the event <code>x = v11</code> is
 * being evaluated. Variable <code>y1</code> is sampled from a distribution with two values
 * <code>v21</code> and <code>v22</code> that each occur once. This is shown in the
 * following figure:
 * <p>
 * <img src="./doc-files/samplingContext_2.jpg" alt="Image samplingContext_2.jpg does not show, sorry" >
 * <p>
 * The bottom nodes correspond to the events <code>(x = v11 and y = v21)</code> and
 * <code>(x = v11 and y = v22)</code>. Their parent node corresponds to the event
 * <code>x = v11</code>. You see that the counts of all old nodes have to be multiplied
 * by 2, otherwise the new nodes do not fit in.
 * <p>
 * Evaluation continues and at some point we evaluate the event <code>x = v12</code>.
 * We sample another variable <code>y2</code> from a distribution with only one value
 * <code>v3</code>. See the following figure:
 * <p>
 * <img src="./doc-files/samplingContext_3.jpg" alt="Image samplingContext_3.jpg does not show, sorry" >
 * <p>
 * The middle node at the second row corresponds to <code>x = v12</code> while its child
 * corresponds to <code>x = v12 and y2 = v3</code>. We see that these nodes have the
 * same count, because their probability is the same. If <code>x = v12</code>, then
 * there is no other possibility then <code>y2 = v3</code>.
 * <p>
 * Finally, when <code>x = v13</code> then a variable <code>y3</code> is sampled from a distribution of
 * size 12. The option <code>x = v13</code> already occurs 4 times, so when we multiply all
 * counts by 3 then the new distribution fits. See the following figure:
 * <p>
 * <img src="./doc-files/samplingContext_4.jpg" alt="Image samplingContext_4.jpg does not show, sorry" >
 * <p>
 * <p>
 * The following rules have to be applied during the sample process:
 * <ol>
 * <li> A new distribution can be added such that:
 * <ol>
 * <li> The counts and the total of a new distribution may be multiplied by a positive integer before it is added to the tree.
 * <li> The total of the added new distribution should equal the weight of the parent node.
 * </ol>
 * <li> To fit in a new distribution, all existing counts in the tree can be multiplied by a positive integer number.
 * <li> When the tree is multiplied (refined), the output distribution has to be multiplied (refined) by the same number.
 * </ol>
 * @author martijn
 *
 */
public interface SampleContext {
    public void startSampledVariable(final int line, final int column, final Samplable sampledDistribution);
    public void scoreUnknown();
    public void stopSampledVariable();
    public boolean hasNextValue();
    public ProbabilityTreeValue nextValue();
    public void score(Object value);
    public Distribution getResult();
    public boolean isScored();

    public static SampleContext getInstance(boolean isPossibilityCounting) {
        if(isPossibilityCounting) {
            return new SampleContextImpl(new RefinementStrategy.CountingPossibilities());
        } else {
            return new SampleContextImpl(new RefinementStrategy.NotCountingPossibilities());
        }
    }
}
