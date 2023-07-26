package org.orcid.pojo.summary;


import java.util.List;

public class RecordSummary {
    private String name;
    private String orcid;
    private List<AffiliationSummary> employmentAffiliations;
    private int employmentAffiliationsCount;
    private String creation;
    private String lastModified;
    private int validatedWorks;
    private int selfAssertedWorks;
    private int reviews;
    private int peerReviewPublicationGrants;
    private int validatedFunds;
    private int selfAssertedFunds;
    private List<AffiliationSummary> professionalActivities;
    private int professionalActivitiesCount;
    private List<ExternalIdentifiersSummary> externalIdentifiers;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOrcid() {
        return orcid;
    }

    public void setOrcid(String orcid) {
        this.orcid = orcid;
    }

    public List<AffiliationSummary> getEmploymentAffiliations() {
        return employmentAffiliations;
    }

    public void setEmploymentAffiliations(List<AffiliationSummary> employmentAffiliations) {
        this.employmentAffiliations = employmentAffiliations;
    }

    public int getEmploymentAffiliationsCount() {
        return employmentAffiliationsCount;
    }

    public void setEmploymentAffiliationsCount(int employmentAffiliationsCount) {
        this.employmentAffiliationsCount = employmentAffiliationsCount;
    }

    public String getCreation() {
        return creation;
    }

    public void setCreation(String creation) {
        this.creation = creation;
    }

    public String getLastModified() {
        return lastModified;
    }

    public void setLastModified(String lastModified) {
        this.lastModified = lastModified;
    }

    public int getValidatedWorks() {
        return validatedWorks;
    }

    public void setValidatedWorks(int validatedWorks) {
        this.validatedWorks = validatedWorks;
    }

    public int getSelfAssertedWorks() {
        return selfAssertedWorks;
    }

    public void setSelfAssertedWorks(int selfAssertedWorks) {
        this.selfAssertedWorks = selfAssertedWorks;
    }

    public int getReviews() {
        return reviews;
    }

    public void setReviews(int reviews) {
        this.reviews = reviews;
    }

    public int getPeerReviewPublicationGrants() {
        return peerReviewPublicationGrants;
    }

    public void setPeerReviewPublicationGrants(int peerReviewPublicationGrants) {
        this.peerReviewPublicationGrants = peerReviewPublicationGrants;
    }

    public int getValidatedFunds() {
        return validatedFunds;
    }

    public void setValidatedFunds(int validatedFunds) {
        this.validatedFunds = validatedFunds;
    }

    public int getSelfAssertedFunds() {
        return selfAssertedFunds;
    }

    public void setSelfAssertedFunds(int selfAssertedFunds) {
        this.selfAssertedFunds = selfAssertedFunds;
    }

    public List<AffiliationSummary> getProfessionalActivities() {
        return professionalActivities;
    }

    public void setProfessionalActivities(List<AffiliationSummary> professionalActivities) {
        this.professionalActivities = professionalActivities;
    }

    public int getProfessionalActivitiesCount() {
        return professionalActivitiesCount;
    }

    public void setProfessionalActivitiesCount(int professionalActivitiesCount) {
        this.professionalActivitiesCount = professionalActivitiesCount;
    }

    public List<ExternalIdentifiersSummary> getExternalIdentifiers() {
        return externalIdentifiers;
    }

    public void setExternalIdentifiers(List<ExternalIdentifiersSummary> externalIdentifiers) {
        this.externalIdentifiers = externalIdentifiers;
    }


}