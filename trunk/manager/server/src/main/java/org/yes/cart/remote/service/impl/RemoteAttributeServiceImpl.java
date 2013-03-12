/*
 * Copyright 2009 Igor Azarnyi, Denys Pavlov
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

package org.yes.cart.remote.service.impl;

import org.yes.cart.domain.dto.AttributeDTO;
import org.yes.cart.exception.UnableToCreateInstanceException;
import org.yes.cart.exception.UnmappedInterfaceException;
import org.yes.cart.remote.service.RemoteAttributeService;
import org.yes.cart.service.dto.DtoAttributeService;

import java.util.List;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 15-May-2011
 * Time: 17:22:15
 */
public class RemoteAttributeServiceImpl
        extends AbstractRemoteService<AttributeDTO>
        implements RemoteAttributeService {


    /**
     * Construct remote service.
     *
     * @param dtoAttributeService dto service.
     */
    public RemoteAttributeServiceImpl(final DtoAttributeService dtoAttributeService) {
        super(dtoAttributeService);
    }

    /**
     * {@inheritDoc}
     */
    public List<AttributeDTO> findByAttributeGroupCode(final String attributeGroupCode)
            throws UnmappedInterfaceException, UnableToCreateInstanceException {
        return ((DtoAttributeService) getGenericDTOService()).findByAttributeGroupCode(attributeGroupCode);
    }

    /**
     * {@inheritDoc}
     */
    public List<AttributeDTO> findAvailableAttributes(final String attributeGroupCode,
                                                      final List<String> assignedAttributeCodes)
            throws UnmappedInterfaceException, UnableToCreateInstanceException {
        return ((DtoAttributeService) getGenericDTOService()).findAvailableAttributes(attributeGroupCode, assignedAttributeCodes);
    }

    /**
     * {@inheritDoc}
     */
    public List<AttributeDTO> findAvailableAttributesByProductTypeId(final long productTypeId)
            throws UnmappedInterfaceException, UnableToCreateInstanceException {
        return ((DtoAttributeService) getGenericDTOService()).findAvailableAttributesByProductTypeId(productTypeId);
    }

    /**
     * {@inheritDoc}
     */
    public List<AttributeDTO> findAvailableImageAttributesByGroupCode(final String attributeGroupCode)
            throws UnmappedInterfaceException, UnableToCreateInstanceException {
        return ((DtoAttributeService) getGenericDTOService()).findAvailableImageAttributesByGroupCode(attributeGroupCode);
    }

    /**
     * {@inheritDoc}
     */
    public List<AttributeDTO> findAttributesWithMultipleValues(final String attributeGroupCode)
            throws UnmappedInterfaceException, UnableToCreateInstanceException {
        return ((DtoAttributeService) getGenericDTOService()).findAttributesWithMultipleValues(attributeGroupCode);
    }
}
