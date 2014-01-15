/* ***** BEGIN LICENSE BLOCK *****
 * Version: MPL 1.1/GPL 2.0/LGPL 2.1
 *
 * The contents of this file are subject to the Mozilla Public License Version
 * 1.1 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * http://www.mozilla.org/MPL/
 *
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License
 * for the specific language governing rights and limitations under the
 * License.
 *
 * The Original Code is part of dcm4che, an implementation of DICOM(TM) in
 * Java(TM), hosted at https://github.com/gunterze/dcm4che.
 *
 * The Initial Developer of the Original Code is
 * Agfa Healthcare.
 * Portions created by the Initial Developer are Copyright (C) 2011-2014
 * the Initial Developer. All Rights Reserved.
 *
 * Contributor(s):
 * See @authors listed below
 *
 * Alternatively, the contents of this file may be used under the terms of
 * either the GNU General Public License Version 2 or later (the "GPL"), or
 * the GNU Lesser General Public License Version 2.1 or later (the "LGPL"),
 * in which case the provisions of the GPL or the LGPL are applicable instead
 * of those above. If you wish to allow use of your version of this file only
 * under the terms of either the GPL or the LGPL, and not to allow others to
 * use your version of this file under the terms of the MPL, indicate your
 * decision by deleting the provisions above and replace them with the notice
 * and other provisions required by the GPL or the LGPL. If you do not delete
 * the provisions above, a recipient may use your version of this file under
 * the terms of any one of the MPL, the GPL or the LGPL.
 *
 * ***** END LICENSE BLOCK ***** */

package org.dcm4chee.archive.store.impl;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.dcm4che.data.Attributes;
import org.dcm4che.net.service.DicomServiceException;
import org.dcm4chee.archive.entity.Instance;
import org.dcm4chee.archive.entity.Patient;
import org.dcm4chee.archive.entity.Series;
import org.dcm4chee.archive.entity.Study;
import org.dcm4chee.archive.store.StoreContext;
import org.dcm4chee.archive.store.StoreService;

/**
 * @author Gunter Zeilinger <gunterze@gmail.com>
 *
 */
@Stateless
public class StoreServiceEJB {

    @PersistenceContext(unitName="dcm4chee-arc")
    private EntityManager em;

//    @Inject
//    private StoreService storeService;

    public void updateDB(StoreContext storeContext)
            throws DicomServiceException {
        
        StoreService storeService = storeContext.getService();
        
        Instance instance = storeService.findInstance(em, storeContext);
        if (instance != null && !storeService.replaceInstance(em, storeContext, instance))
            return;

        Attributes storedAttrs = storeContext.getAttributes();
        Attributes coercedAtts = storeContext.getCoercedAttributes();
        Series series = storeService.findSeries(em, storeContext);
        if (series == null) {
            Study study = storeService.findStudy(em, storeContext);
            if (study == null) {
                Patient patient = storeService.findPatient(em, storeContext);
                if (patient == null) {
                    patient = storeService.createPatient(em, storeContext);
                } else {
                    storeService.updatePatient(storeContext, patient);
                    storedAttrs.update(patient.getAttributes(), coercedAtts);
                }
                study = storeService.createStudy(em, storeContext, patient);
            } else {
                Patient patient = study.getPatient();
                storeService.updatePatient(storeContext, patient);
                storeService.updateStudy(storeContext, study);
                storedAttrs.update(patient.getAttributes(), coercedAtts);
                storedAttrs.update(study.getAttributes(), coercedAtts);
            }
            series = storeService.createSeries(em, storeContext, study);
        } else {
            Study study = series.getStudy();
            Patient patient = study.getPatient();
            storeService.updatePatient(storeContext, patient);
            storeService.updateStudy(storeContext, study);
            storeService.updateSeries(storeContext, series);
            storedAttrs.update(patient.getAttributes(), coercedAtts);
            storedAttrs.update(study.getAttributes(), coercedAtts);
            storedAttrs.update(series.getAttributes(), coercedAtts);
        }
        instance = storeService.createInstance(em, storeContext, series);
        storeService.createFileRef(em, storeContext, instance);
     }

}