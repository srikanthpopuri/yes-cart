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

import org.junit.Before;
import org.junit.Test;
import org.yes.cart.BaseCoreDBTestCase;
import org.yes.cart.constants.DtoServiceSpringKeys;
import org.yes.cart.domain.dto.ProductTypeAttrDTO;
import org.yes.cart.domain.dto.ProductTypeDTO;
import org.yes.cart.domain.dto.factory.DtoFactory;
import org.yes.cart.service.dto.DtoAttributeService;
import org.yes.cart.service.dto.DtoProductTypeAttrService;
import org.yes.cart.service.dto.DtoProductTypeService;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 09-May-2011
 * Time: 14:12:54
 */
public class DtoProductTypeAttrServiceImplTezt extends BaseCoreDBTestCase {

    private DtoProductTypeAttrService dtoService;
    private DtoProductTypeService dtoProductTypeService;
    private DtoAttributeService dtoAttributeService;
    private DtoFactory dtoFactory;
    public static final String RANGE_NAV = "<rangeList serialization=\"custom\"><unserializable-parents/><list><default><size>10</size></default><int>10</int><range><range><first class=\"string\">0.10</first><second class=\"string\">1.00</second></range></range><range><range><first class=\"string\">1.00</first><second class=\"string\">2.00</second></range></range><range><range><first class=\"string\">2.00</first><second class=\"string\">3.00</second></range></range><range><range><first class=\"string\">3.00</first><second class=\"string\">4.00</second></range></range><range><range><first class=\"string\">4.00</first><second class=\"string\">5.00</second></range></range><range><range><first class=\"string\">5.00</first><second class=\"string\">6.00</second></range></range><range><range><first class=\"string\">6.00</first><second class=\"string\">7.00</second></range></range><range><range><first class=\"string\">7.00</first><second class=\"string\">8.00</second></range></range><range><range><first class=\"string\">8.00</first><second class=\"string\">10.00</second></range></range><range><range><first class=\"string\">10.00</first><second class=\"string\">20.00</second></range></range></list></rangeList>";

    @Before
    public void setUp() {
        dtoService = (DtoProductTypeAttrService) ctx().getBean(DtoServiceSpringKeys.DTO_PRODUCT_TYPE_ATTR_SERVICE);
        dtoProductTypeService = (DtoProductTypeService) ctx().getBean(DtoServiceSpringKeys.DTO_PRODUCT_TYPE_SERVICE);
        dtoAttributeService = (DtoAttributeService) ctx().getBean(DtoServiceSpringKeys.DTO_ATTRIBUTE_SERVICE);
        dtoFactory = (DtoFactory) ctx().getBean(DtoServiceSpringKeys.DTO_FACTORY);
        super.setUp();
    }

    @Test
    public void testCreate() throws Exception {
        ProductTypeAttrDTO dtoProductTypeAttr = getDto();
        dtoProductTypeAttr = dtoService.create(dtoProductTypeAttr);
        assertTrue(dtoProductTypeAttr.getProductTypeAttrId() > 0);
    }

    @Test
    public void testUpdate() throws Exception {
        ProductTypeAttrDTO dtoProductTypeAttr = getDto();
        dtoProductTypeAttr = dtoService.create(dtoProductTypeAttr);
        assertTrue(dtoProductTypeAttr.getProductTypeAttrId() > 0);
        dtoProductTypeAttr.setNavigation(true);
        dtoProductTypeAttr.setNavigationType("R");
        dtoProductTypeAttr.setProducttypeId(1L);
        dtoProductTypeAttr.setRangeNavigation(RANGE_NAV);
        dtoProductTypeAttr.setSimilarity(true);
        dtoProductTypeAttr.setVisible(true);
        dtoProductTypeAttr = dtoService.update(dtoProductTypeAttr);
        assertTrue(dtoProductTypeAttr.isNavigation());
        assertTrue(dtoProductTypeAttr.isSimilarity());
        assertTrue(dtoProductTypeAttr.isVisible());
        assertEquals("R", dtoProductTypeAttr.getNavigationType());
        assertEquals(1, dtoProductTypeAttr.getProducttypeId());
        assertEquals(RANGE_NAV, dtoProductTypeAttr.getRangeNavigation());
    }

    private ProductTypeAttrDTO getDto() throws Exception {
        ProductTypeAttrDTO dtoProductTypeAttr = dtoFactory.getByIface(ProductTypeAttrDTO.class);
        dtoProductTypeAttr.setAttributeDTO(dtoAttributeService.getById(7000L));
        dtoProductTypeAttr.setNavigation(false);
        dtoProductTypeAttr.setNavigationType("S");
        ProductTypeDTO productTypeDTO = dtoProductTypeService.getById(1L);
        dtoProductTypeAttr.setProducttypeId(productTypeDTO.getProducttypeId());
        dtoProductTypeAttr.setRangeNavigation(null);
        dtoProductTypeAttr.setSimilarity(false);
        dtoProductTypeAttr.setVisible(false);
        return dtoProductTypeAttr;
    }
}
