/**
 * =============================================================================
 *
 * ORCID (R) Open Source
 * http://orcid.org
 *
 * Copyright (c) 2012-2013 ORCID, Inc.
 * Licensed under an MIT-Style License (MIT)
 * http://orcid.org/open-source-license
 *
 * This copyright and license information (including a link to the full license)
 * shall be included in its entirety in all copies or substantial portion of
 * the software.
 *
 * =============================================================================
 */
package org.orcid.frontend.web.controllers;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.StringUtils;
import org.eclipse.jetty.util.ajax.JSON;
import org.orcid.core.manager.EmailManager;
import org.orcid.core.manager.EncryptionManager;
import org.orcid.core.manager.NotificationManager;
import org.orcid.core.manager.OrcidSearchManager;
import org.orcid.core.manager.OrcidSocialManager;
import org.orcid.core.manager.OtherNameManager;
import org.orcid.core.manager.ProfileEntityManager;
import org.orcid.core.manager.ProfileKeywordManager;
import org.orcid.core.manager.ResearcherUrlManager;
import org.orcid.frontend.web.forms.ChangePersonalInfoForm;
import org.orcid.frontend.web.forms.ChangeSecurityQuestionForm;
import org.orcid.frontend.web.forms.ManagePasswordOptionsForm;
import org.orcid.frontend.web.forms.PreferencesForm;
import org.orcid.jaxb.model.message.ApprovalDate;
import org.orcid.jaxb.model.message.CreditName;
import org.orcid.jaxb.model.message.DelegateSummary;
import org.orcid.jaxb.model.message.Delegation;
import org.orcid.jaxb.model.message.DelegationDetails;
import org.orcid.jaxb.model.message.Email;
import org.orcid.jaxb.model.message.EncryptedSecurityAnswer;
import org.orcid.jaxb.model.message.Keywords;
import org.orcid.jaxb.model.message.OrcidIdentifier;
import org.orcid.jaxb.model.message.OrcidProfile;
import org.orcid.jaxb.model.message.OtherNames;
import org.orcid.jaxb.model.message.Preferences;
import org.orcid.jaxb.model.message.ResearcherUrl;
import org.orcid.jaxb.model.message.ResearcherUrls;
import org.orcid.jaxb.model.message.ScopePathType;
import org.orcid.jaxb.model.message.SecurityDetails;
import org.orcid.jaxb.model.message.SecurityQuestionId;
import org.orcid.jaxb.model.message.Url;
import org.orcid.jaxb.model.message.UrlName;
import org.orcid.jaxb.model.message.Visibility;
import org.orcid.jaxb.model.message.WorkExternalIdentifierType;
import org.orcid.password.constants.OrcidPasswordConstants;
import org.orcid.persistence.dao.EmailDao;
import org.orcid.persistence.dao.GivenPermissionToDao;
import org.orcid.persistence.dao.ProfileDao;
import org.orcid.persistence.jpa.entities.EmailEntity;
import org.orcid.persistence.jpa.entities.GivenPermissionToEntity;
import org.orcid.persistence.jpa.entities.ProfileEntity;
import org.orcid.persistence.jpa.entities.ProfileSummaryEntity;
import org.orcid.persistence.jpa.entities.ResearcherUrlEntity;
import org.orcid.pojo.ChangePassword;
import org.orcid.pojo.ManageDelegate;
import org.orcid.pojo.SecurityQuestion;
import org.orcid.pojo.ajaxForm.BiographyForm;
import org.orcid.pojo.ajaxForm.CountryForm;
import org.orcid.pojo.ajaxForm.Emails;
import org.orcid.pojo.ajaxForm.Errors;
import org.orcid.pojo.ajaxForm.NamesForm;
import org.orcid.pojo.ajaxForm.Text;
import org.orcid.utils.DateUtils;
import org.orcid.utils.OrcidWebUtils;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.MapBindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.NoSuchRequestHandlingMethodException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Copyright 2011-2012 ORCID
 * 
 * @author Declan Newman (declan) Date: 22/02/2012
 */
@Controller("manageProfileController")
@RequestMapping(value = { "/account", "/manage" })
public class ManageProfileController extends BaseWorkspaceController {

    private static final String IS_SELF = "isSelf";

    private static final String FOUND = "found";

    /*
     * session attribute that is used to see if we should check and notify the
     * user if their primary email ins't verified.
     */
    public static String CHECK_EMAIL_VALIDATED = "CHECK_EMAIL_VALIDATED";

    @Resource
    private OrcidSearchManager orcidSearchManager;

    @Resource
    private EncryptionManager encryptionManager;

    @Resource
    private NotificationManager notificationManager;

    @Resource
    private ResearcherUrlManager researcherUrlManager;

    @Resource
    private ProfileKeywordManager profileKeywordManager;

    @Resource
    private OtherNameManager otherNameManager;

    @Resource
    private ProfileEntityManager profileEntityManager;

    @Resource
    private ProfileDao profileDao;

    @Resource
    private GivenPermissionToDao givenPermissionToDao;

    @Resource
    private EmailDao emailDao;

    @Resource
    private OrcidSocialManager orcidSocialManager;
    
    @Resource 
    private EmailManager emailManager;

    public EncryptionManager getEncryptionManager() {
        return encryptionManager;
    }

    public void setEncryptionManager(EncryptionManager encryptionManager) {
        this.encryptionManager = encryptionManager;
    }

    public void setNotificationManager(NotificationManager notificationManager) {
        this.notificationManager = notificationManager;
    }

    public void setResearcherUrlManager(ResearcherUrlManager researcherUrlManager) {
        this.researcherUrlManager = researcherUrlManager;
    }

    public void setProfileKeywordManager(ProfileKeywordManager profileKeywordManager) {
        this.profileKeywordManager = profileKeywordManager;
    }

    public void setOtherNameManager(OtherNameManager otherNameManager) {
        this.otherNameManager = otherNameManager;
    }

    public void setGivenPermissionToDao(GivenPermissionToDao givenPermissionToDao) {
        this.givenPermissionToDao = givenPermissionToDao;
    }

    public void setProfileEntityManager(ProfileEntityManager profileEntityManager) {
        this.profileEntityManager = profileEntityManager;
    }

    @ModelAttribute("visibilities")
    public Map<String, String> retrieveVisibilitiesAsMap() {
        Map<String, String> visibilities = new LinkedHashMap<String, String>();
        visibilities.put(Visibility.PUBLIC.value(), "Public");
        visibilities.put(Visibility.LIMITED.value(), "Limited");
        visibilities.put(Visibility.PRIVATE.value(), "Private");
        return visibilities;
    }

    @ModelAttribute("externalIdentifierRefData")
    public Map<String, String> retrieveExternalIdentifierRefData() {
        Map<String, String> types = new HashMap<String, String>();
        for (WorkExternalIdentifierType type : WorkExternalIdentifierType.values()) {
            types.put(type.value(), buildInternationalizationKey(WorkExternalIdentifierType.class, type.value()));
        }
        return types;
    }

    @RequestMapping
    public ModelAndView manageProfile(@RequestParam(value = "activeTab", required = false) String activeTab) {
        String tab = activeTab == null ? "profile-tab" : activeTab;
        ModelAndView mav = rebuildManageView(tab);
        return mav;
    }
        
    @ModelAttribute("hasVerifiedEmail")
    public boolean hasVerifiedEmail() {
        OrcidProfile profile = getEffectiveProfile();
        if (profile == null  || profile.getOrcidBio() == null || profile.getOrcidBio().getContactDetails() == null) return false;
        return profile.getOrcidBio().getContactDetails().anyEmailVerified();
    }    
    

    @RequestMapping(value = "/search-for-delegate-by-email/{email}/")
    public @ResponseBody
    Map<String, Boolean> searchForDelegateByEmail(@PathVariable String email) {
        Map<String, Boolean> map = new HashMap<>();
        EmailEntity emailEntity = emailDao.findCaseInsensitive(email);
        if (emailEntity == null) {
            map.put(FOUND, Boolean.FALSE);
            return map;
        } else {
            map.put(FOUND, Boolean.TRUE);
            map.put(IS_SELF, emailEntity.getProfile().getId().equals(getCurrentUserOrcid()));
            return map;
        }
    }

    @RequestMapping(value = "/confirm-delegate", method = RequestMethod.POST)
    public ModelAndView confirmDelegate(@ModelAttribute("delegateOrcid") String delegateOrcid) {
        OrcidProfile delegateProfile = orcidProfileManager.retrieveOrcidProfile(delegateOrcid);
        ModelAndView mav = new ModelAndView("confirm_delegate");
        mav.addObject("delegateProfile", delegateProfile);
        return mav;
    }

    @RequestMapping(value = "/delegates.json", method = RequestMethod.GET)
    public @ResponseBody
    Delegation getDelegatesJson(HttpServletRequest request) throws NoSuchRequestHandlingMethodException {
        OrcidProfile currentProfile = getEffectiveProfile();
        Delegation delegation = currentProfile.getOrcidBio().getDelegation();
        return delegation;
    }

    @RequestMapping(value = "/addDelegate.json")
    public @ResponseBody
    ManageDelegate addDelegate(@RequestBody ManageDelegate addDelegate) {
        // Check password
        String password = addDelegate.getPassword();
        if (StringUtils.isBlank(password) || !encryptionManager.hashMatches(password, getEffectiveProfile().getPassword())) {
            addDelegate.getErrors().add(getMessage("check_password_modal.incorrect_password"));
            return addDelegate;
        }
        String currentUserOrcid = getCurrentUserOrcid();
        String delegateOrcid = addDelegate.getDelegateToManage();
        GivenPermissionToEntity existing = givenPermissionToDao.findByGiverAndReceiverOrcid(currentUserOrcid, delegateOrcid);
        if (existing == null) {
            // Clear the delegate's profile from the cache so that the granting
            // user is visible to them immediately
            Date delegateLastModified = profileDao.updateLastModifiedDate(delegateOrcid);
            GivenPermissionToEntity permission = new GivenPermissionToEntity();
            permission.setGiver(currentUserOrcid);
            ProfileSummaryEntity receiver = new ProfileSummaryEntity(delegateOrcid);
            receiver.setLastModified(delegateLastModified);
            permission.setReceiver(receiver);
            permission.setApprovalDate(new Date());
            givenPermissionToDao.merge(permission);
            OrcidProfile currentUser = getEffectiveProfile();
            ProfileEntity delegateProfile = profileEntityManager.findByOrcid(delegateOrcid);
            DelegationDetails details = new DelegationDetails();
            details.setApprovalDate(new ApprovalDate(DateUtils.convertToXMLGregorianCalendar(permission.getApprovalDate())));
            DelegateSummary summary = new DelegateSummary();
            details.setDelegateSummary(summary);
            summary.setOrcidIdentifier(new OrcidIdentifier(delegateOrcid));
            String creditName = delegateProfile.getCreditName();
            if (StringUtils.isNotBlank(creditName)) {
                summary.setCreditName(new CreditName(creditName));
            }
            List<DelegationDetails> detailsList = new ArrayList<>(1);
            detailsList.add(details);
            notificationManager.sendNotificationToAddedDelegate(currentUser, detailsList);
        }
        return addDelegate;
    }

    @RequestMapping(value = "/addDelegateByEmail.json")
    public @ResponseBody
    ManageDelegate addDelegateByEmail(@RequestBody ManageDelegate addDelegate) {
        EmailEntity emailEntity = emailDao.findCaseInsensitive(addDelegate.getDelegateEmail());
        addDelegate.setDelegateToManage(emailEntity.getProfile().getId());
        return addDelegate(addDelegate);
    }

    @RequestMapping(value = "/revokeDelegate.json", method = RequestMethod.POST)
    public @ResponseBody
    ManageDelegate revokeDelegate(@RequestBody ManageDelegate manageDelegate) {
        // Check password
        String password = manageDelegate.getPassword();
        if (StringUtils.isBlank(password) || !encryptionManager.hashMatches(password, getEffectiveProfile().getPassword())) {
            manageDelegate.getErrors().add(getMessage("check_password_modal.incorrect_password"));
            return manageDelegate;
        }
        String giverOrcid = getCurrentUserOrcid();
        orcidProfileManager.revokeDelegate(giverOrcid, manageDelegate.getDelegateToManage());
        return manageDelegate;
    }

    @RequestMapping(value = "/revoke-delegate-from-summary-view", method = RequestMethod.GET)
    public ModelAndView revokeDelegateFromSummaryView(@RequestParam("orcid") String receiverOrcid) {
        String giverOrcid = getCurrentUserOrcid();
        orcidProfileManager.revokeDelegate(giverOrcid, receiverOrcid);
        ModelAndView mav = new ModelAndView("redirect:/account/view-account-settings");
        return mav;
    }

    @RequestMapping(value = "/revoke-application")
    public ModelAndView revokeApplication(@RequestParam("applicationOrcid") String applicationOrcid,
            @RequestParam(value = "scopePaths", required = false, defaultValue = "") String[] scopePaths) {
        String userOrcid = getCurrentUserOrcid();
        orcidProfileManager.revokeApplication(userOrcid, applicationOrcid, ScopePathType.getScopesFromStrings(Arrays.asList(scopePaths)));
        ModelAndView mav = new ModelAndView("redirect:/account?activeTab=application-tab");
        return mav;
    }

    @RequestMapping(value = "/revoke-application-from-summary-view", method = RequestMethod.GET)
    public ModelAndView revokeApplicationFromSummaryView(@RequestParam("applicationOrcid") String applicationOrcid, @RequestParam("scopePaths") String[] scopePaths) {
        String userOrcid = getCurrentUserOrcid();
        orcidProfileManager.revokeApplication(userOrcid, applicationOrcid, ScopePathType.getScopesFromStrings(Arrays.asList(scopePaths)));
        ModelAndView mav = new ModelAndView("redirect:/account/view-account-settings");
        return mav;
    }

    @RequestMapping(value = "/admin-switch-user", method = RequestMethod.GET)
    public ModelAndView adminSwitchUser(HttpServletRequest request, @RequestParam("orcid") String targetOrcid, RedirectAttributes redirectAttributes) {
        // Redirect to the new way of switching user, which includes admin
        // access
        ModelAndView mav = null;
        if (StringUtils.isNotBlank(targetOrcid))
            targetOrcid = targetOrcid.trim();
        if (profileEntityManager.orcidExists(targetOrcid)) {
            mav = new ModelAndView("redirect:/switch-user?j_username=" + targetOrcid);
        } else {
            redirectAttributes.addFlashAttribute("invalidOrcid", true);
            mav = new ModelAndView("redirect:/my-orcid");
        }
        return mav;
    }

    protected ModelAndView rebuildManageView(String activeTab) {
        ModelAndView mav = new ModelAndView("manage");
        mav.addObject("showPrivacy", true);
        OrcidProfile profile = getEffectiveProfile();
        mav.addObject("managePasswordOptionsForm", populateManagePasswordFormFromUserInfo());
        mav.addObject("preferencesForm", new PreferencesForm(profile));
        mav.addObject("profile", profile);
        mav.addObject("activeTab", activeTab);
        mav.addObject("securityQuestions", getSecurityQuestions());
        return mav;
    }

    private ManagePasswordOptionsForm populateManagePasswordFormFromUserInfo() {
        OrcidProfile profile = getEffectiveProfile();

        // TODO - placeholder just to test the retrieve etc..replace with only
        // fields that we will populate
        // password fields are never populated
        OrcidProfile unecryptedProfile = orcidProfileManager.retrieveOrcidProfile(profile.getOrcidIdentifier().getPath());
        ManagePasswordOptionsForm managePasswordOptionsForm = new ManagePasswordOptionsForm();
        managePasswordOptionsForm.setVerificationNumber(unecryptedProfile.getVerificationCode());
        managePasswordOptionsForm.setSecurityQuestionAnswer(unecryptedProfile.getSecurityQuestionAnswer());
        Integer securityQuestionId = null;
        SecurityDetails securityDetails = unecryptedProfile.getOrcidInternal().getSecurityDetails();
        // TODO - confirm that security details aren't null and that we can
        // change schema to be an int for security
        // questions field
        if (securityDetails != null) {
            securityQuestionId = securityDetails.getSecurityQuestionId() != null ? new Integer((int) securityDetails.getSecurityQuestionId().getValue()) : null;
        }

        managePasswordOptionsForm.setSecurityQuestionId(securityQuestionId);

        return managePasswordOptionsForm;
    }

    @ModelAttribute("changeNotificationsMap")
    public Map<String, String> getChangeNotificationsMap() {
        Map<String, String> changeNotificationsMap = new HashMap<String, String>();
        changeNotificationsMap.put("sendMe", "");
        return changeNotificationsMap;
    }

    @ModelAttribute("orcidNewsMap")
    public Map<String, String> getOrcidNewsMap() {
        Map<String, String> orcidNewsMap = new HashMap<String, String>();
        orcidNewsMap.put("sendMe", "");
        return orcidNewsMap;
    }

    @ModelAttribute("colleagueConfirmedRegistrationMap")
    public Map<String, String> getColleagueConfirmedRegistrationMap() {
        Map<String, String> otherNewsMap = new HashMap<String, String>();
        otherNewsMap.put("sendMe", "");
        return otherNewsMap;
    }

    @ModelAttribute("securityQuestions")
    public Map<String, String> getSecurityQuestions() {
        Map<String, String> securityQuestions = securityQuestionManager.retrieveSecurityQuestionsAsInternationalizedMap();
        Map<String, String> securityQuestionsWithMessages = new LinkedHashMap<String, String>();
        for (String key : securityQuestions.keySet()) {
            securityQuestionsWithMessages.put(key, getMessage(securityQuestions.get(key)));
        }
        return securityQuestionsWithMessages;
    }

    @RequestMapping(value = "/view-account-settings", method = RequestMethod.GET)
    public String viewAccountSettings() {
        // Defunct page, redirect to main account page in case of bookmarks.
        return "redirect:/account";
    }

    @RequestMapping(value = { "/security-question", "/change-security-question" }, method = RequestMethod.GET)
    public ModelAndView viewChangeSecurityQuestion() {
        return populateChangeSecurityDetailsViewFromUserProfile(new ChangeSecurityQuestionForm());
    }

    @RequestMapping(value = { "/security-question", "/change-security-question" }, method = RequestMethod.POST)
    public ModelAndView updateWithChangedSecurityQuestion(@ModelAttribute("changeSecurityQuestionForm") @Valid ChangeSecurityQuestionForm changeSecurityQuestionForm,
            BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            ModelAndView changeSecurityDetailsView = new ModelAndView("change_security_question");
            changeSecurityDetailsView.addAllObjects(bindingResult.getModel());
            changeSecurityDetailsView.addObject(changeSecurityQuestionForm);
            return changeSecurityDetailsView;
        }
        OrcidProfile profile = getEffectiveProfile();
        profile.setSecurityQuestionAnswer(changeSecurityQuestionForm.getSecurityQuestionAnswer());
        profile.getOrcidInternal().getSecurityDetails().setSecurityQuestionId(new SecurityQuestionId(changeSecurityQuestionForm.getSecurityQuestionId()));
        orcidProfileManager.updateSecurityQuestionInformation(profile);
        ModelAndView changeSecurityDetailsView = populateChangeSecurityDetailsViewFromUserProfile(changeSecurityQuestionForm);
        changeSecurityDetailsView.addObject("securityQuestionSaved", true);
        return changeSecurityDetailsView;

    }

    @RequestMapping(value = "/security-question.json", method = RequestMethod.GET)
    public @ResponseBody
    SecurityQuestion getSecurityQuestionJson(HttpServletRequest request) {
        OrcidProfile profile = getEffectiveProfile();
        SecurityDetails sd = profile.getOrcidInternal().getSecurityDetails();
        SecurityQuestionId securityQuestionId = sd.getSecurityQuestionId();
        EncryptedSecurityAnswer encryptedSecurityAnswer = sd.getEncryptedSecurityAnswer();

        if (securityQuestionId == null) {
            sd.getSecurityQuestionId();
            securityQuestionId = new SecurityQuestionId();
        }

        SecurityQuestion securityQuestion = new SecurityQuestion();
        securityQuestion.setSecurityQuestionId(securityQuestionId.getValue());

        if (encryptedSecurityAnswer != null) {
            securityQuestion.setSecurityAnswer(encryptionManager.decryptForInternalUse(encryptedSecurityAnswer.getContent()));
        }

        return securityQuestion;
    }

    @RequestMapping(value = "/security-question.json", method = RequestMethod.POST)
    public @ResponseBody
    SecurityQuestion setSecurityQuestionJson(HttpServletRequest request, @RequestBody SecurityQuestion securityQuestion) {
        List<String> errors = new ArrayList<String>();
        if (securityQuestion.getSecurityQuestionId() != 0 && (securityQuestion.getSecurityAnswer() == null || securityQuestion.getSecurityAnswer().trim() == ""))
            errors.add(getMessage("manage.pleaseProvideAnAnswer"));

        if (securityQuestion.getPassword() == null || !encryptionManager.hashMatches(securityQuestion.getPassword(), getEffectiveProfile().getPassword())) {
            errors.add(getMessage("check_password_modal.incorrect_password"));
        }

        // If the security question is empty, clean the security answer field
        if (securityQuestion.getSecurityQuestionId() == 0)
            securityQuestion.setSecurityAnswer(new String());

        if (errors.size() == 0) {
            OrcidProfile profile = getEffectiveProfile();
            if (profile.getOrcidInternal().getSecurityDetails().getSecurityQuestionId() == null)
                profile.getOrcidInternal().getSecurityDetails().setSecurityQuestionId(new SecurityQuestionId());
            profile.getOrcidInternal().getSecurityDetails().getSecurityQuestionId().setValue(securityQuestion.getSecurityQuestionId());

            if (profile.getOrcidInternal().getSecurityDetails().getEncryptedSecurityAnswer() == null)
                profile.getOrcidInternal().getSecurityDetails().setEncryptedSecurityAnswer(new EncryptedSecurityAnswer());
            profile.setSecurityQuestionAnswer(securityQuestion.getSecurityAnswer());
            orcidProfileManager.updateSecurityQuestionInformation(profile);
            errors.add(getMessage("manage.securityQuestionUpdated"));
        }

        securityQuestion.setErrors(errors);
        return securityQuestion;
    }

    @RequestMapping(value = "/preferences.json", method = RequestMethod.GET)
    public @ResponseBody
    Preferences getDefaultPreference(HttpServletRequest request) {
        OrcidProfile profile = getEffectiveProfile();
        profile.getOrcidInternal().getPreferences();
        return profile.getOrcidInternal().getPreferences() != null ? profile.getOrcidInternal().getPreferences() : new Preferences();
    }

    @RequestMapping(value = "/preferences.json", method = RequestMethod.POST)
    public @ResponseBody
    Preferences setDefaultPreference(HttpServletRequest request, @RequestBody Preferences preferences) {
        orcidProfileManager.updatePreferences(getCurrentUserOrcid(), preferences);
        return preferences;
    }

    private ModelAndView populateChangeSecurityDetailsViewFromUserProfile(ChangeSecurityQuestionForm changeSecurityQuestionForm) {
        ModelAndView changeSecurityDetailsView = new ModelAndView("change_security_question");

        Integer securityQuestionId = null;

        SecurityDetails securityDetails = getEffectiveProfile().getOrcidInternal().getSecurityDetails();
        if (securityDetails != null) {
            securityQuestionId = securityDetails.getSecurityQuestionId() != null ? new Integer((int) securityDetails.getSecurityQuestionId().getValue()) : null;

        }

        String encryptedSecurityAnswer = securityDetails != null && securityDetails.getEncryptedSecurityAnswer() != null ? securityDetails.getEncryptedSecurityAnswer()
                .getContent() : null;
        String securityAnswer = StringUtils.isNotBlank(encryptedSecurityAnswer) ? encryptionManager.decryptForInternalUse(encryptedSecurityAnswer) : "";

        changeSecurityQuestionForm.setSecurityQuestionId(securityQuestionId);
        changeSecurityQuestionForm.setSecurityQuestionAnswer(securityAnswer);

        changeSecurityDetailsView.addObject("changeSecurityQuestionForm", changeSecurityQuestionForm);
        return changeSecurityDetailsView;
    }

    @RequestMapping(value = { "/change-password.json" }, method = RequestMethod.GET)
    public @ResponseBody
    ChangePassword getChangedPasswordJson(HttpServletRequest request) {
        ChangePassword p = new ChangePassword();
        return p;
    }

    @RequestMapping(value = { "/change-password.json" }, method = RequestMethod.POST)
    public @ResponseBody
    ChangePassword changedPasswordJson(HttpServletRequest request, @RequestBody ChangePassword cp) {
        List<String> errors = new ArrayList<String>();

        if (cp.getPassword() == null || !cp.getPassword().matches(OrcidPasswordConstants.ORCID_PASSWORD_REGEX)) {
            errors.add(getMessage("NotBlank.registrationForm.confirmedPassword"));
        } else if (!cp.getPassword().equals(cp.getRetypedPassword())) {
            errors.add(getMessage("FieldMatch.registrationForm"));
        }

        if (cp.getOldPassword() == null || !encryptionManager.hashMatches(cp.getOldPassword(), getEffectiveProfile().getPassword())) {
            errors.add(getMessage("orcid.frontend.change.password.current_password_incorrect"));
        }

        if (errors.size() == 0) {
            OrcidProfile profile = getEffectiveProfile();
            profile.setPassword(cp.getPassword());
            orcidProfileManager.updatePasswordInformation(profile);
            cp = new ChangePassword();
            errors.add(getMessage("orcid.frontend.change.password.change.successfully"));
        }
        cp.setErrors(errors);
        return cp;
    }

    @RequestMapping(value = { "deactivate-orcid", "/view-deactivate-orcid-account" }, method = RequestMethod.GET)
    public ModelAndView viewDeactivateOrcidAccount() {
        ModelAndView deactivateOrcidView = new ModelAndView("deactivate_orcid");
        return deactivateOrcidView;
    }

    @RequestMapping(value = "/confirm-deactivate-orcid/{encryptedEmail}", method = RequestMethod.GET)
    public ModelAndView confirmDeactivateOrcidAccount(HttpServletRequest request, @PathVariable("encryptedEmail") String encryptedEmail,
            RedirectAttributes redirectAttributes) throws Exception {
        ModelAndView result = null;
        String decryptedEmail = encryptionManager.decryptForExternalUse(new String(Base64.decodeBase64(encryptedEmail), "UTF-8"));
        OrcidProfile profile = getEffectiveProfile();
        // Since all profiles have at least one email address, this must never
        // be null

        String primaryEmail = profile.getOrcidBio().getContactDetails().retrievePrimaryEmail().getValue();

        if (decryptedEmail.equals(primaryEmail)) {
            orcidProfileManager.deactivateOrcidProfile(profile);
            result = new ModelAndView("redirect:/signout#deactivated");
        } else {
            redirectAttributes.addFlashAttribute("emailDoesntMatch", true);
            return new ModelAndView("redirect:/my-orcid");
        }

        return result;
    }

    @RequestMapping(value = "/manage-bio-settings", method = RequestMethod.GET)
    public ModelAndView viewEditBio(HttpServletRequest request) {
        ModelAndView manageBioView = new ModelAndView("manage_bio_settings");
        OrcidProfile profile = getEffectiveProfile();
        ChangePersonalInfoForm changePersonalInfoForm = new ChangePersonalInfoForm(profile);
        manageBioView.addObject("changePersonalInfoForm", changePersonalInfoForm);
        return manageBioView;
    }

    @RequestMapping(value = "/verifyEmail.json", method = RequestMethod.GET)
    public @ResponseBody
    Errors verifyEmailJson(HttpServletRequest request, @RequestParam("email") String email) {
        OrcidProfile currentProfile = getEffectiveProfile();
        String primaryEmail = currentProfile.getOrcidBio().getContactDetails().retrievePrimaryEmail().getValue();
        if (primaryEmail.equals(email))
            request.getSession().setAttribute(ManageProfileController.CHECK_EMAIL_VALIDATED, false);

        URI baseUri = OrcidWebUtils.getServerUriWithContextPath(request);
        notificationManager.sendVerificationEmail(currentProfile, baseUri, email);
        return new Errors();
    }

    @RequestMapping(value = "/delayVerifyEmail.json", method = RequestMethod.GET)
    public @ResponseBody
    Errors delayVerifyEmailJson(HttpServletRequest request) {
        request.getSession().setAttribute(ManageProfileController.CHECK_EMAIL_VALIDATED, false);
        return new Errors();
    }

    @RequestMapping(value = "/send-deactivate-account.json", method = RequestMethod.GET)
    public @ResponseBody
    Email startDeactivateOrcidAccountJson(HttpServletRequest request) {
        URI uri = OrcidWebUtils.getServerUriWithContextPath(request);
        OrcidProfile currentProfile = getEffectiveProfile();
        Email email = currentProfile.getOrcidBio().getContactDetails().retrievePrimaryEmail();
        notificationManager.sendOrcidDeactivateEmail(currentProfile, uri);
        return email;
    }

    @SuppressWarnings("unchecked")
    @RequestMapping(value = "/emails.json", method = RequestMethod.GET)
    public @ResponseBody
    org.orcid.pojo.ajaxForm.Emails getEmailsJson(HttpServletRequest request) throws NoSuchRequestHandlingMethodException {
        OrcidProfile currentProfile = getEffectiveProfile();
        Emails emails = new org.orcid.pojo.ajaxForm.Emails();
        emails.setEmails((List<org.orcid.pojo.Email>) (Object) currentProfile.getOrcidBio().getContactDetails().getEmail());
        return emails;
    }

    @RequestMapping(value = "/addEmail.json", method = RequestMethod.POST)
    public @ResponseBody
    org.orcid.pojo.Email addEmailsJson(HttpServletRequest request, @RequestBody org.orcid.pojo.AddEmail email) {
        List<String> errors = new ArrayList<String>();
        // Check password
        if (email.getPassword() == null || !encryptionManager.hashMatches(email.getPassword(), getEffectiveProfile().getPassword())) {
            errors.add(getMessage("check_password_modal.incorrect_password"));
        }

        if (errors.isEmpty()) {
            String newPrime = null;
            String oldPrime = null;
            List<String> emailErrors = new ArrayList<String>();

            // clear errros
            email.setErrors(new ArrayList<String>());

            // if blank
            if (email.getValue() == null || email.getValue().trim().equals("")) {
                emailErrors.add(getMessage("Email.personalInfoForm.email"));
            }
            OrcidProfile currentProfile = getEffectiveProfile();
            List<Email> emails = currentProfile.getOrcidBio().getContactDetails().getEmail();

            MapBindingResult mbr = new MapBindingResult(new HashMap<String, String>(), "Email");
            // make sure there are no dups
            validateEmailAddress(email.getValue(), false, request, mbr);

            for (ObjectError oe : mbr.getAllErrors()) {
                emailErrors.add(getMessage(oe.getCode(), email.getValue()));
            }
            email.setErrors(emailErrors);

            if (emailErrors.size() == 0) {
                if (email.isPrimary()) {
                    for (Email curEmail : emails) {
                        if (curEmail.isPrimary())
                            oldPrime = curEmail.getValue();
                        curEmail.setPrimary(false);
                    }
                    newPrime = email.getValue();
                }

                emails.add(email);
                currentProfile.getOrcidBio().getContactDetails().setEmail(emails);
                email.setSource(getRealUserOrcid());
                emailManager.addEmail(currentProfile.getOrcidIdentifier().getPath(), email);

                // send verifcation email for new address
                URI baseUri = OrcidWebUtils.getServerUriWithContextPath(request);
                notificationManager.sendVerificationEmail(currentProfile, baseUri, email.getValue());

                // if primary also send change notification.
                if (newPrime != null && !newPrime.equalsIgnoreCase(oldPrime)) {
                    request.getSession().setAttribute(ManageProfileController.CHECK_EMAIL_VALIDATED, false);
                    notificationManager.sendEmailAddressChangedNotification(currentProfile, new Email(oldPrime), baseUri);
                }

            }
        } else {
            email.setErrors(errors);
        }

        return email;
    }

    @RequestMapping(value = "/deleteEmail.json", method = RequestMethod.DELETE)
    public @ResponseBody
    org.orcid.pojo.Email deleteEmailJson(HttpServletRequest request, @RequestBody org.orcid.pojo.Email email) {
        List<String> emailErrors = new ArrayList<String>();

        // clear errros
        email.setErrors(new ArrayList<String>());

        // if blank
        if (email.getValue() == null || email.getValue().trim().equals("")) {
            emailErrors.add(getMessage("Email.personalInfoForm.email"));
        }
        OrcidProfile currentProfile = getEffectiveProfile();
        List<Email> emails = currentProfile.getOrcidBio().getContactDetails().getEmail();

        if (email.isPrimary()) {
            emailErrors.add(getMessage("manage.email.primaryEmailDeletion"));
        }

        email.setErrors(emailErrors);

        if (emailErrors.size() == 0) {
            Iterator<Email> emailIterator = emails.iterator();
            while (emailIterator.hasNext()) {
                Email nextEmail = emailIterator.next();
                if (nextEmail.getValue().equals(email.getValue())) {
                    emailIterator.remove();
                }
            }
            currentProfile.getOrcidBio().getContactDetails().setEmail(emails);
            emailManager.removeEmail(currentProfile.getOrcidIdentifier().getPath(), email.getValue());
        }
        return email;
    }

    @SuppressWarnings("unchecked")
    @RequestMapping(value = "/emails.json", method = RequestMethod.POST)
    public @ResponseBody
    org.orcid.pojo.ajaxForm.Emails postEmailsJson(HttpServletRequest request, @RequestBody org.orcid.pojo.ajaxForm.Emails emails) {
        org.orcid.pojo.Email newPrime = null;
        org.orcid.pojo.Email oldPrime = null;
        List<String> allErrors = new ArrayList<String>();

        for (org.orcid.pojo.Email email : emails.getEmails()) {

            MapBindingResult mbr = new MapBindingResult(new HashMap<String, String>(), "Email");
            validateEmailAddress(email.getValue(), request, mbr);
            List<String> emailErrors = new ArrayList<String>();
            for (ObjectError oe : mbr.getAllErrors()) {
                String msg = getMessage(oe.getCode(), email.getValue());
                emailErrors.add(getMessage(oe.getCode(), email.getValue()));
                allErrors.add(msg);
            }
            email.setErrors(emailErrors);
            if (email.isPrimary())
                newPrime = email;
        }

        if (newPrime == null) {
            allErrors.add("A Primary Email Must be selected");
        }

        OrcidProfile currentProfile = getEffectiveProfile();
        if (currentProfile.getOrcidBio().getContactDetails().retrievePrimaryEmail() != null)
            oldPrime = new org.orcid.pojo.Email(currentProfile.getOrcidBio().getContactDetails().retrievePrimaryEmail());

        emails.setErrors(allErrors);
        if (allErrors.size() == 0) {
            currentProfile.getOrcidBio().getContactDetails().setEmail((List<Email>) (Object) emails.getEmails());
            emailManager.updateEmails(currentProfile.getOrcidIdentifier().getPath(), currentProfile.getOrcidBio().getContactDetails().getEmail());
            if (newPrime != null && !newPrime.getValue().equalsIgnoreCase(oldPrime.getValue())) {
                URI baseUri = OrcidWebUtils.getServerUriWithContextPath(request);
                notificationManager.sendEmailAddressChangedNotification(currentProfile, new Email(oldPrime.getValue()), baseUri);
                if (!newPrime.isVerified()) {
                    notificationManager.sendVerificationEmail(currentProfile, baseUri, newPrime.getValue());
                    request.getSession().setAttribute(ManageProfileController.CHECK_EMAIL_VALIDATED, false);
                }
            }
        }
        return emails;
    }

    @RequestMapping(value = "/countryForm.json", method = RequestMethod.GET)
    public @ResponseBody
    CountryForm getProfileCountryJson(HttpServletRequest request) throws NoSuchRequestHandlingMethodException {
        OrcidProfile currentProfile = getEffectiveProfile();
        CountryForm countryForm = CountryForm.valueOf(currentProfile);
        //Set country name
        if(countryForm != null && countryForm.getIso2Country() != null) {
            Map<String, String> countries = retrieveIsoCountries();
            if(countries != null)
                countryForm.setCountryName(countries.get(countryForm.getIso2Country().getValue().name()));
        }
        
        return countryForm;
    }

    @RequestMapping(value = "/countryForm.json", method = RequestMethod.POST)
    public @ResponseBody
    CountryForm setProfileCountryJson(HttpServletRequest request, @RequestBody CountryForm countryForm) throws NoSuchRequestHandlingMethodException {
        OrcidProfile currentProfile = getEffectiveProfile();
        countryForm.populateProfile(currentProfile);
        // only update entity attributes
        orcidProfileManager.updateCountry(currentProfile);
        return countryForm;
    }

    @RequestMapping(value = "/nameForm.json", method = RequestMethod.GET)
    public @ResponseBody
    NamesForm getNameForm(HttpServletRequest request) throws NoSuchRequestHandlingMethodException {
        OrcidProfile currentProfile = getEffectiveProfile();
        NamesForm nf = NamesForm.valueOf(currentProfile.getOrcidBio().getPersonalDetails());
        return nf;
    }

    @RequestMapping(value = "/nameForm.json", method = RequestMethod.POST)
    public @ResponseBody
    NamesForm setNameFormJson(HttpServletRequest request, @RequestBody NamesForm nf) throws NoSuchRequestHandlingMethodException {
        nf.setErrors(new ArrayList<String>());
        if (nf.getGivenNames() == null)
            nf.setGivenNames(new Text());
        givenNameValidate(nf.getGivenNames());
        copyErrors(nf.getGivenNames(), nf);
        if (nf.getErrors().size() > 0)
            return nf;
        OrcidProfile currentProfile = getEffectiveProfile();
        nf.populatePersonalDetails(currentProfile.getOrcidBio().getPersonalDetails());
        orcidProfileManager.updateNames(currentProfile);
        return nf;
    }

    @RequestMapping(value = "/biographyForm.json", method = RequestMethod.GET)
    public @ResponseBody
    BiographyForm getBiographyForm(HttpServletRequest request) throws NoSuchRequestHandlingMethodException {
        OrcidProfile currentProfile = getEffectiveProfile();
        BiographyForm bf = BiographyForm.valueOf(currentProfile);
        return bf;
    }

    @RequestMapping(value = "/biographyForm.json", method = RequestMethod.POST)
    public @ResponseBody
    BiographyForm setBiographyFormJson(HttpServletRequest request, @RequestBody BiographyForm bf) throws NoSuchRequestHandlingMethodException {
        bf.setErrors(new ArrayList<String>());
        validateBiography(bf.getBiography());
        copyErrors(bf.getBiography(), bf);
        if (bf.getErrors().size() > 0)
            return bf;
        OrcidProfile currentProfile = getEffectiveProfile();
        bf.populateProfile(currentProfile);
        orcidProfileManager.updateBiography(currentProfile);
        return bf;
    }

    @RequestMapping(value = "/save-bio-settings", method = RequestMethod.POST)
    public ModelAndView saveEditedBio(HttpServletRequest request, @Valid @ModelAttribute("changePersonalInfoForm") ChangePersonalInfoForm changePersonalInfoForm,
            BindingResult bindingResult, RedirectAttributes redirectAttributes) {
        ModelAndView manageBioView = new ModelAndView("redirect:/account/manage-bio-settings");

        for (String keyword : changePersonalInfoForm.getKeywordsAsList()) {
            if (keyword.length() > ChangePersonalInfoForm.KEYWORD_MAX_LEN) {
                bindingResult.rejectValue("keywordsDelimited", "Length.changePersonalInfoForm.keywordsDelimited");
                break;
            }
        }

        if (bindingResult.hasErrors()) {
            ModelAndView erroredView = new ModelAndView("manage_bio_settings");

            // If an error happens and the user doesnt have any website,
            // the privacy selector for websites dissapears.
            // In order to fix this, if the ChangePersonalInfoForm doesnt have
            // any researcher url, we add a new one with an empty list, which
            // is different than null ResearcherUrls
            Map<String, Object> model = bindingResult.getModel();

            if (changePersonalInfoForm.getSavedResearcherUrls() == null) {
                changePersonalInfoForm.setSavedResearcherUrls(new ResearcherUrls());
            }

            model.put("changePersonalInfoForm", changePersonalInfoForm);

            erroredView.addAllObjects(bindingResult.getModel());
            return erroredView;
        }

        OrcidProfile profile = getEffectiveProfile();
        // Update profile with values that comes from user request
        changePersonalInfoForm.mergeOrcidBioDetails(profile);

        // Update profile on database
        profileEntityManager.updateProfile(profile);

        String orcid = profile.getOrcidIdentifier().getPath();

        // Update other names on database
        OtherNames otherNames = profile.getOrcidBio().getPersonalDetails().getOtherNames();
        otherNameManager.updateOtherNames(orcid, otherNames);

        // Update keywords on database
        Keywords keywords = profile.getOrcidBio().getKeywords();
        profileKeywordManager.updateProfileKeyword(orcid, keywords);

        // Update researcher urls on database
        ResearcherUrls researcherUrls = profile.getOrcidBio().getResearcherUrls();
        boolean hasErrors = researcherUrlManager.updateResearcherUrls(orcid, researcherUrls);
        // TODO: The researcherUrlManager will not store any duplicated
        // researcher url on database,
        // however there is no way to tell the controller that some of the
        // researcher urls were not
        // saved, so, if an error occurs, we need to reload researcher ids from
        // database and update
        // cached profile. A more efficient way to fix this might be used.
        if (hasErrors) {
            ResearcherUrls upToDateResearcherUrls = getUpToDateResearcherUrls(orcid, researcherUrls.getVisibility());
            profile.getOrcidBio().setResearcherUrls(upToDateResearcherUrls);
        }

        redirectAttributes.addFlashAttribute("changesSaved", true);
        return manageBioView;
    }

    /**
     * Generate an up to date ResearcherUrls object.
     * 
     * @param orcid
     * @param visibility
     * */
    private ResearcherUrls getUpToDateResearcherUrls(String orcid, Visibility visibility) {
        ResearcherUrls upTodateResearcherUrls = new ResearcherUrls();
        upTodateResearcherUrls.setVisibility(visibility);
        List<ResearcherUrlEntity> upToDateResearcherUrls = researcherUrlManager.getResearcherUrls(orcid);

        for (ResearcherUrlEntity researcherUrlEntity : upToDateResearcherUrls) {
            ResearcherUrl newResearcherUrl = new ResearcherUrl();
            newResearcherUrl.setUrl(new Url(researcherUrlEntity.getUrl()));
            newResearcherUrl.setUrlName(new UrlName(researcherUrlEntity.getUrlName()));
            upTodateResearcherUrls.getResearcherUrl().add(newResearcherUrl);
        }

        return upTodateResearcherUrls;
    }

    /**
     * Check if the user have twitter enabled
     * */
    @RequestMapping(value = { "/twitter/check-twitter-status" }, method = RequestMethod.GET)
    public @ResponseBody
    boolean isTwitterEnabled() {
        String orcid = getEffectiveUserOrcid();
        return orcidSocialManager.isTwitterEnabled(orcid);
    }

    /**
     * Get a user request to authorize twitter and return the authorization URL
     * */
    @RequestMapping(value = { "/twitter" }, method = RequestMethod.POST)
    public @ResponseBody
    String goToTwitterAuthPage() throws Exception {
        String authUrl = orcidSocialManager.getTwitterAuthorizationUrl(getEffectiveUserOrcid());
        return authUrl;
    }

    /**
     * Get the twitter credentials and enable it on the user profile
     * */
    @RequestMapping(value = { "/twitter" }, method = RequestMethod.GET)
    public ModelAndView setTwitterKeyToProfileGET(@RequestParam("oauth_token") String token, @RequestParam("oauth_verifier") String verifier) throws Exception {
        OrcidProfile profile = getEffectiveProfile();
        ModelAndView mav = new ModelAndView("manage");
        mav.addObject("showPrivacy", true);
        mav.addObject("managePasswordOptionsForm", populateManagePasswordFormFromUserInfo());
        mav.addObject("preferencesForm", new PreferencesForm(profile));
        mav.addObject("profile", profile);
        mav.addObject("activeTab", "profile-tab");
        mav.addObject("securityQuestions", getSecurityQuestions());
        if (profile != null) {
            orcidSocialManager.enableTwitter(getEffectiveUserOrcid(), verifier);
            mav.addObject("twitter", true);
        }
        return mav;
    }

    /**
     * Disable twitter access
     * */
    @RequestMapping(value = { "/disable-twitter" }, method = RequestMethod.POST)
    public @ResponseBody
    boolean disableTwitter() throws Exception {
        String orcid = getEffectiveUserOrcid();
        orcidSocialManager.disableTwitter(orcid);
        return true;
    }

    /**
     * Authorize a delegate request done by an admin
     * */
    @RequestMapping(value = {"/authorize-delegates"}, method = RequestMethod.GET)
    public ModelAndView authorizeDelegatesRequest(@RequestParam("key") String key) {
        ModelAndView mav = new ModelAndView("manage");        
        //Set default objects the manage page needs
        mav.addObject("showPrivacy", true);
        mav.addObject("managePasswordOptionsForm", populateManagePasswordFormFromUserInfo());
        mav.addObject("preferencesForm", new PreferencesForm(getEffectiveProfile()));
        mav.addObject("profile", getEffectiveProfile());
        mav.addObject("activeTab", "profile-tab");
        mav.addObject("securityQuestions", getSecurityQuestions());
        
        try {
            Map<String, String> params = decryptDelegationKey(key);
            if(params.containsKey(AdminController.MANAGED_USER_PARAM) && params.containsKey(AdminController.TRUSTED_USER_PARAM)) {
                String managedOrcid = params.get(AdminController.MANAGED_USER_PARAM);
                String trustedOrcid = params.get(AdminController.TRUSTED_USER_PARAM);
                //Check if managed user is the same than the logged user
                if(managedOrcid.equals(getEffectiveUserOrcid())) {
                    //Check if the managed user email is verified, if not, verify it
                    verifyPrimaryEmailIfNeeded(managedOrcid);
                    //Check if the delegation doesnt exists
                    GivenPermissionToEntity existing = givenPermissionToDao.findByGiverAndReceiverOrcid(managedOrcid, trustedOrcid);
                    if(existing == null) {
                        // Clear the delegate's profile from the cache so that the granting
                        // user is visible to them immediately
                        Date delegateLastModified = profileDao.updateLastModifiedDate(trustedOrcid);
                        GivenPermissionToEntity permission = new GivenPermissionToEntity();
                        permission.setGiver(managedOrcid);
                        ProfileSummaryEntity receiver = new ProfileSummaryEntity(trustedOrcid);
                        receiver.setLastModified(delegateLastModified);
                        permission.setReceiver(receiver);
                        permission.setApprovalDate(new Date());
                        givenPermissionToDao.merge(permission);
                        OrcidProfile currentUser = getEffectiveProfile();
                        ProfileEntity delegateProfile = profileEntityManager.findByOrcid(trustedOrcid);
                        DelegationDetails details = new DelegationDetails();
                        details.setApprovalDate(new ApprovalDate(DateUtils.convertToXMLGregorianCalendar(permission.getApprovalDate())));
                        DelegateSummary summary = new DelegateSummary();
                        details.setDelegateSummary(summary);
                        summary.setOrcidIdentifier(new OrcidIdentifier(trustedOrcid));
                        String creditName = delegateProfile.getCreditName();
                        if (StringUtils.isNotBlank(creditName)) {
                            summary.setCreditName(new CreditName(creditName));
                        }
                        List<DelegationDetails> detailsList = new ArrayList<>(1);
                        detailsList.add(details);                                                            
                        //Send notifications
                        notificationManager.sendNotificationToAddedDelegate(currentUser, detailsList);                    
                    }                                
                    mav.addObject("admin_delegate_approved", getMessage("admin.delegate.success", trustedOrcid));
                } else {
                    //Exception, the email was not for you
                    mav.addObject("admin_delegate_failed", getMessage("admin.delegate.error.invalid_user"));
                }            
            } else {
                //Error
                mav.addObject("admin_delegate_failed", getMessage("admin.delegate.error.invalid_link"));
            }
        } catch(UnsupportedEncodingException uee) {
            mav.addObject("admin_delegate_failed", getMessage("admin.delegate.error.invalid_link"));
        }
        
        return mav;
    }
    
    /**
     * @throws UnsupportedEncodingException 
     * */
    @SuppressWarnings("unchecked")
    private Map<String, String> decryptDelegationKey(String encryptedKey) throws UnsupportedEncodingException  {
        String jsonString = encryptionManager.decryptForExternalUse(new String(Base64.decodeBase64(encryptedKey), "UTF-8"));                
        Map<String, String> params = (HashMap<String, String>)JSON.parse(jsonString);
        return params;
    }
    
    /**
     * Verify a primary email if it is not verified yet.
     * @param orcid
     *          The profile id to check
     * */
    private void verifyPrimaryEmailIfNeeded(String orcid) {
        if(!emailManager.isPrimaryEmailVerified(orcid)) {
            emailManager.verifyPrimaryEmail(orcid);
        }
    }
    
}