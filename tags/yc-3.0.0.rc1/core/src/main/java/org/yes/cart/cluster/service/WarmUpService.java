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

package org.yes.cart.cluster.service;

/**
 * Warm up service for storefront nodes. Allows to preload all essential long
 * term caches so that it does try to do so in heavy load.
 *
 * User: denispavlov
 * Date: 13-10-17
 * Time: 6:59 PM
 */
public interface WarmUpService {

    /**
     * Perform the server warm up.
     */
    void warmUp();

}
