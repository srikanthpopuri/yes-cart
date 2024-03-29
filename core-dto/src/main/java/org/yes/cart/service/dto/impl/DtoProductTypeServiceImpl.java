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

package org.yes.cart.service.dto.impl;

import com.inspiresoftware.lib.dto.geda.adapter.repository.AdaptersRepository;
import org.yes.cart.domain.dto.ProductTypeDTO;
import org.yes.cart.domain.dto.factory.DtoFactory;
import org.yes.cart.domain.dto.impl.ProductTypeDTOImpl;
import org.yes.cart.domain.entity.ProductType;
import org.yes.cart.service.domain.GenericService;
import org.yes.cart.service.dto.DtoProductTypeService;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 09-May-2011
 * Time: 14:12:54
 */
public class DtoProductTypeServiceImpl
        extends AbstractDtoServiceImpl<ProductTypeDTO, ProductTypeDTOImpl, ProductType>
        implements DtoProductTypeService {


    /**
     * Construct base remote service.
     *
     * @param dtoFactory    {@link org.yes.cart.domain.dto.factory.DtoFactory}
     * @param productTypeGenericService       {@link org.yes.cart.service.domain.GenericService}
     */
    public DtoProductTypeServiceImpl(
            final GenericService<ProductType> productTypeGenericService,
            final DtoFactory dtoFactory,
            final AdaptersRepository adaptersRepository) {
        super(dtoFactory, productTypeGenericService, adaptersRepository);
    }



    /**
     * Get the dto interface.
     *
     * @return dto interface.
     */
    public Class<ProductTypeDTO> getDtoIFace() {
        return ProductTypeDTO.class;
    }

    /**
     * Get the dto implementation class.
     *
     * @return dto implementation class.
     */
    public Class<ProductTypeDTOImpl> getDtoImpl() {
        return ProductTypeDTOImpl.class;
    }

    /**
     * Get the entity interface.
     *
     * @return entity interface.
     */
    public Class<ProductType> getEntityIFace() {
        return ProductType.class;
    }
}
