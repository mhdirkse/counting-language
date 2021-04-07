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
 * This interface has a static factory method that can create two kinds of instances, one
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
 * dividing the (new) weight of the parent node by the new distribution's size (resulting in the weight of the previous diagram)
 * and multiplied that with the count of the value within the wheel distribution.
 * <p>
 * Possibilities counts are only meaningful for experiments that
 * consist of a series of sub-experiments. The distribution sizes of these sub-experiments
 * may be different, but these sizes may not depend on the sampled values. If this rule would
 * be violated, one would have to work with conditional probabilities that can not be interpreted
 * anymore as possibility counts. The only exception is that sub-experiments may be omitted depending
 * on the values sampled from earlier sub-experiments. The interpretation is then that the omitted
 * sub experiment is done, but that the outcome does not influence the result of the experiment.
 * <p>
 * From the example, we see that the nodes of the probability tree have to be managed as follows.
 * <ul>
 * <li>Each sampled distribution corresponds to a row in the probability tree.
 * <li>Each node in a row corresponds to a value within the distribution.
 * <li>When a new distribution (row) is added, all existing nodes in the tree are multiplied by the size of the new distribution.
 * <li>The weight of each new node equals the count of the node's value within the new distribution.
 * </ul>
 * 
 * <h2>Scoring results</h2>
 * 
 * <h2>Experiments without meaningful possibility counts</h2>
 *
 * To calculate the result distribution, probability trees are used as shown in
 * the following picture:
 * <p>
 * <img src="./doc-files/samplingContext_1.jpg" alt="Image samplingContext_1.jpg does not show, sorry" >
 * <p>
 * This tree results when sampling the first variable,
 * say <code>x</code>, has started. This sample variable comes from a distribution with three values,
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
 * <li> The ratios of the new counts are the same as the ratios of the original distribution.
 * <li> The total of the new distribution should equal the count of the parent node.
 * </ol>
 * <li> To fit in a new distribution, all existing counts in the tree can be multiplied by an integer number.
 * <li> When the tree is multiplied, the result distribution has to be multiplied by the same number.
 * </ol>
 * @author martijn
 *
 */
public interface SampleContext extends SampleContextBase {
    public void score(Object value);
    public void scoreUnknown();
    public Distribution getResult();
    public boolean isScored();

    public static SampleContext getInstance(boolean isPossibilityCounting) {
        if(isPossibilityCounting) {
            return new SampleContextImpl(new PossibilityCountingValidityStrategy.CountingPossibilities());
        } else {
            return new SampleContextImpl(new PossibilityCountingValidityStrategy.NotCountingPossibilities());
        }
    }
}
