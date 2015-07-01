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

package org.yes.cart.bulkimport.image.impl;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yes.cart.bulkimport.image.ImageImportDomainObjectStrategy;
import org.yes.cart.bulkimport.model.ImportDescriptor;
import org.yes.cart.bulkimport.service.ImportService;
import org.yes.cart.bulkimport.service.impl.AbstractImportService;
import org.yes.cart.service.async.JobStatusListener;
import org.yes.cart.service.async.model.JobContext;
import org.yes.cart.service.async.model.JobContextKeys;
import org.yes.cart.service.domain.ImageService;
import org.yes.cart.service.federation.FederationFacade;
import org.yes.cart.service.image.ImageNameStrategy;

import java.io.File;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.Set;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 12/12/11
 * Time: 10:35 AM
 */
public class ImagesBulkImportServiceImpl extends AbstractImportService implements ImportService {

    private final Logger LOG = LoggerFactory.getLogger(ImagesBulkImportServiceImpl.class);

    private final ImageService imageService;

    private final ImageImportDomainObjectStrategy[] strategies;

    /**
     * Construct bilk import service.
     *
     * @param federationFacade federation facade
     * @param imageService image service
     * @param strategies   domain strategies to associate image with domain model
     */
    public ImagesBulkImportServiceImpl(final FederationFacade federationFacade,
                                       final ImageService imageService,
                                       final ImageImportDomainObjectStrategy[] strategies) {
        super(federationFacade);
        this.imageService = imageService;
        this.strategies = strategies;
    }

    /**
     * {@inheritDoc}
     */
    public BulkImportResult doImport(final JobContext context) {

        final JobStatusListener statusListener = context.getListener();
        final Set<String> importedFiles = context.getAttribute(JobContextKeys.IMPORT_FILE_SET);
        final String fileName = context.getAttribute(JobContextKeys.IMPORT_FILE);
        final String imageVaultRootDirectory = context.getAttribute(JobContextKeys.IMAGE_VAULT_PATH);
        final ImportDescriptor importDescriptor = context.getAttribute(JobContextKeys.IMPORT_DESCRIPTOR);
        final String imageImportDescriptorName = context.getAttribute(JobContextKeys.IMPORT_DESCRIPTOR_NAME);

        final String regExp = importDescriptor.getImportFileDescriptor().getFileNameMask();

        String info = MessageFormat.format(
                "start images import with {0} path using {1} and file mask {2}",
                importDescriptor.getImportDirectory(),
                imageImportDescriptorName,
                regExp);
        statusListener.notifyMessage(info);
        LOG.info(info);
        File[] files = getFilesToImport(importDescriptor, fileName);
        if (files != null) {
            info = MessageFormat.format(
                    "\nINFO found {0} images to import",
                    files.length);
            statusListener.notifyMessage(info);
            LOG.info(info);
            int count = 0;
            int total = files.length;
            for (File file : files) {
                doImport(file, importDescriptor, statusListener, importedFiles, imageVaultRootDirectory);
                statusListener.notifyPing("Processed " + (++count) + " of " + total + " images");
            }

        }
        return BulkImportResult.OK;

    }

    /**
     * Performs import of single image file. With following workflow:
     * first locate the product by code, if product found then insert / update image attribute.
     * The try to locate sku by code, if sku found, then insert / update image attribute.
     * If product or sku image attribute was inserted or update, that copy file to particular folder.
     *
     * @param file          file to import
     * @param importDescriptor descriptor
     * @param statusListener error report
     * @param importedFiles add file to this set if imported it successfully imported.
     * @param imageVaultRootDirectory path to image vault
     */
    private void doImport(final File file,
                          final ImportDescriptor importDescriptor,
                          final JobStatusListener statusListener,
                          final Set<String> importedFiles,
                          final String imageVaultRootDirectory) {

        final ImageNameStrategy strategy = imageService.getImageNameStrategy(importDescriptor.getSelectSql());

        final String fileName = file.getName();
        final String code = strategy.resolveObjectCode(fileName);
        final String locale = strategy.resolveLocale(fileName);
        final String suffix = strategy.resolveSuffix(fileName);

        boolean success = false;
        for (final ImageImportDomainObjectStrategy domainStrategy : strategies) {
            if (domainStrategy.supports(strategy.getUrlPath())) {
                success |= domainStrategy.doImageImport(statusListener, fileName, code, suffix, locale);
            }
        }

        if (success) {
            try {

                String newFileName = imageService.addImageToRepository(
                        file.getName(),
                        code,
                        FileUtils.readFileToByteArray(file),
                        strategy.getUrlPath(),
                        imageVaultRootDirectory);
                final String info = MessageFormat.format(
                        "image {0} {1} added to image repository", file.getAbsolutePath(), newFileName);
                statusListener.notifyMessage(info);
                LOG.info(info);

            } catch (IOException e) {
                final String err = MessageFormat.format(
                        "can not add {0} to image repository. Try to add it manually. Error is {1}", file.getAbsolutePath(), e.getMessage());
                LOG.error(err, e);
                statusListener.notifyError(err);
            }
        }

        importedFiles.add(file.getAbsolutePath());

    }

}
