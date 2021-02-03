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

package com.github.mhdirkse.countlang.execution;

import com.github.mhdirkse.countlang.algorithm.Distribution;

/**
 * This class does the calculations behind sampling variables from distributions and
 * scoring values in a result distribution. This way, counting-language statements
 * expressing a probability experiment are evaluated, the result being a
 * {@link Distribution}. A {@link Distribution} is like a multi-set, the count of
 * each element being a non-negative integer. If a value <code>v</code> has count
 * <code>c</code> in the result distribution and if the total count of the result
 * distribution is <code>n</code>, then this encodes that the probability
 * of <code>v</code> is the rational number <code>c / n</code>.
 * <p>
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
    public void score(int value);
    public void scoreUnknown();
    public Distribution getResult();
    public boolean isScored();

    public static SampleContext getInstance() {
        return new SampleContextImpl();
    }
}
