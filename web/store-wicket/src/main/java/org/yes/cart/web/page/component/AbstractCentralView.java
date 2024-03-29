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

package org.yes.cart.web.page.component;

import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.yes.cart.domain.entity.Category;
import org.yes.cart.domain.entity.Seo;
import org.yes.cart.domain.entity.Seoable;
import org.yes.cart.domain.queryobject.NavigationContext;
import org.yes.cart.util.ShopCodeContext;
import org.yes.cart.web.support.constants.StorefrontServiceSpringKeys;
import org.yes.cart.web.support.service.CategoryServiceFacade;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 7/12/11
 * Time: 10:11 PM
 */
public abstract class AbstractCentralView extends BaseComponent {

    @SpringBean(name = StorefrontServiceSpringKeys.CATEGORY_SERVICE_FACADE)
    protected CategoryServiceFacade categoryServiceFacade;

    private final long categoryId;

    private final NavigationContext navigationContext;

    private transient Category category;

    /**
      * Construct panel.
     *
     * @param id panel id
     * @param categoryId  current category id.
     * @param navigationContext navigation context.
     */
    public AbstractCentralView(final String id, final long categoryId, final NavigationContext navigationContext) {
        super(id);
        this.categoryId = categoryId;
        this.navigationContext = navigationContext;
    }

    /**
     * Get category id.
     * @return current category id.
     */
    public long getCategoryId() {
        return categoryId;
    }

    /**
     * Get current query.
     * @return  query.
     */
    public NavigationContext getNavigationContext() {
        return navigationContext;
    }

    /**
     * Get category.
     * @return {@link Category}
     */
    public Category getCategory() {
        if (category == null) {
            category = categoryServiceFacade.getCategory(categoryId, ShopCodeContext.getShopId());
        }
        return category;
    }


    /**
     * Get page title.
     * @return page title
     */
    public IModel<String> getPageTitle() {
        final Seoable seoable = getSeoObject();
        if (seoable != null) {
            final String lang = getLocale().getLanguage();
            final String title = getPageTitle(seoable.getSeo(), lang);
            if (title != null) {
                return new Model<String>(title);
            }
        }
        return null;
    }


    protected String getPageTitle(final Seo seo, final String language) {
        if (seo != null) {
            final String title = getI18NSupport().getFailoverModel(seo.getDisplayTitle(), seo.getTitle()).getValue(language);
            if (org.apache.commons.lang.StringUtils.isNotBlank(title)) {
                return title;
            }
        }
        return null;
    }

    /**
     * Hook for subclasses to utilise Seo mechanism
     *
     * @return main seo object for given page
     */
    protected Seoable getSeoObject() {
        return getCategory();
    }


    /**
     * Get opage description
     * @return description
     */
    public IModel<String> getDescription() {
        final Seoable seoable = getSeoObject();
        if (seoable != null) {
            final String lang = getLocale().getLanguage();
            final String title = getDescription(seoable.getSeo(), lang);
            if (title != null) {
                return new Model<String>(title);
            }
        }
        return null;

    }


    protected String getDescription(final Seo seo, final String language) {
        if (seo != null) {
            final String desc = getI18NSupport().getFailoverModel(seo.getDisplayMetadescription(), seo.getMetadescription()).getValue(language);
            if (org.apache.commons.lang.StringUtils.isNotBlank(desc)) {
                return desc;
            }
        }
        return null;
    }


    /**
     * Get keywords.
     * @return keywords
     */
    public IModel<String> getKeywords() {
        final Seoable seoable = getSeoObject();
        if (seoable != null) {
            final String lang = getLocale().getLanguage();
            final String title = getKeywords(seoable.getSeo(), lang);
            if (title != null) {
                return new Model<String>(title);
            }
        }
        return null;

    }



    protected String getKeywords(final Seo seo, final String language) {
        if (seo != null) {
            final String desc = getI18NSupport().getFailoverModel(seo.getDisplayMetakeywords(), seo.getMetakeywords()).getValue(language);
            if (org.apache.commons.lang.StringUtils.isNotBlank(desc)) {
                return desc;
            }
        }
        return null;
    }



}
