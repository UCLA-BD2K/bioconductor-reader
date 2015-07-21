import java.util.Arrays;

/**
 * Created by vincekyi on 7/17/15.
 */
public class BioconductorTool {

    private String url;
    private String name;
    private String description;
    private String[] authors;
    private String[] maintainers;
    private String installation;
    private String documentation;
    private String[] biocViews;
    private String version;
    private String license;
    private String[] dependencies;
    private String[] imports;
    private String[] suggestions;
    private String systemReqs;
    private String codeRepo;
    private String packageLink;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String[] getAuthors() {
        return authors;
    }

    public void setAuthors(String[] authors) {
        this.authors = authors;
    }

    public String[] getMaintainers() {
        return maintainers;
    }

    public void setMaintainers(String[] maintainers) {
        this.maintainers = maintainers;
    }

    public String getInstallation() {
        return installation;
    }

    public void setInstallation(String installation) {
        this.installation = installation;
    }

    public String getDocumentation() {
        return documentation;
    }

    public void setDocumentation(String documentation) {
        this.documentation = documentation;
    }

    public String[] getBiocViews() {
        return biocViews;
    }

    public void setBiocViews(String[] biocViews) {
        this.biocViews = biocViews;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getLicense() {
        return license;
    }

    public void setLicense(String license) {
        this.license = license;
    }

    public String[] getDependencies() {
        return dependencies;
    }

    public void setDependencies(String[] dependencies) {
        this.dependencies = dependencies;
    }

    public String[] getImports() {
        return imports;
    }

    public void setImports(String[] imports) {
        this.imports = imports;
    }

    public String[] getSuggestions() {
        return suggestions;
    }

    public void setSuggestions(String[] suggestions) {
        this.suggestions = suggestions;
    }

    public String getSystemReqs() {
        return systemReqs;
    }

    public void setSystemReqs(String systemReqs) {
        this.systemReqs = systemReqs;
    }

    public String getCodeRepo() {
        return codeRepo;
    }

    public void setCodeRepo(String codeRepo) {
        this.codeRepo = codeRepo;
    }

    public String getPackageLink() {
        return packageLink;
    }

    public void setPackageLink(String packageLink) {
        this.packageLink = packageLink;
    }

    @Override
    public String toString() {
        return "BioconductorTool{" +
                "url='" + url + '\'' +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", authors=" + Arrays.toString(authors) +
                ", maintainers=" + Arrays.toString(maintainers) +
                ", installation='" + installation + '\'' +
                ", documentation='" + documentation + '\'' +
                ", biocViews=" + Arrays.toString(biocViews) +
                ", version='" + version + '\'' +
                ", license='" + license + '\'' +
                ", dependencies=" + Arrays.toString(dependencies) +
                ", imports=" + Arrays.toString(imports) +
                ", suggestions=" + Arrays.toString(suggestions) +
                ", systemReqs='" + systemReqs + '\'' +
                ", codeRepo='" + codeRepo + '\'' +
                ", packageLink='" + packageLink + '\'' +
                '}';
    }
}
