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

package org.yes.cart.utils.impl;


import org.yes.cart.domain.entity.CustomerOrder;

import java.util.Comparator;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 09-May-2011
 * Time: 14:12:54
 */
public class CustomerOrderComparator implements Comparator<CustomerOrder> {

    /** {@inheritDoc} */
    public int compare(final CustomerOrder order1, final CustomerOrder order2) {
        return (order1.getCustomerorderId() < order2.getCustomerorderId() ?
                -1 : (order1.getCustomerorderId() == order2.getCustomerorderId() ? 0 : 1));
    }

}
