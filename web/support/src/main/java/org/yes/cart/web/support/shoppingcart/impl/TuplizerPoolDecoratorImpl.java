/*
 * Copyright 2009 Denys Pavlov, Igor Azarnyi
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package org.yes.cart.web.support.shoppingcart.impl;

import org.springframework.aop.TargetSource;

/**
 * User: denispavlov
 * Date: 22/08/2014
 * Time: 00:22
 */
public class TuplizerPoolDecoratorImpl implements TargetSource {

    private final TargetSource tuplizerPool;

    public TuplizerPoolDecoratorImpl(final TargetSource tuplizerPool) {
        this.tuplizerPool = tuplizerPool;
    }

    /** {@inheritDoc} */
    @Override
    public Class<?> getTargetClass() {
        return tuplizerPool.getTargetClass();
    }

    /** {@inheritDoc} */
    @Override
    public boolean isStatic() {
        return tuplizerPool.isStatic();
    }

    /** {@inheritDoc} */
    @Override
    public Object getTarget() throws Exception {
        return tuplizerPool.getTarget();
    }

    /** {@inheritDoc} */
    @Override
    public void releaseTarget(final Object target) throws Exception {
        tuplizerPool.releaseTarget(target);
    }

}
