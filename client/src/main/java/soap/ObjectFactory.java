
package soap;

import javax.xml.namespace.QName;
import jakarta.xml.bind.JAXBElement;
import jakarta.xml.bind.annotation.XmlElementDecl;
import jakarta.xml.bind.annotation.XmlRegistry;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the soap package. 
 * <p>An ObjectFactory allows you to programmatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {

    private static final QName _GetAllResearchers_QNAME = new QName("http://ws/", "getAllResearchers");
    private static final QName _GetAllResearchersResponse_QNAME = new QName("http://ws/", "getAllResearchersResponse");
    private static final QName _GetResearcherByName_QNAME = new QName("http://ws/", "getResearcherByName");
    private static final QName _GetResearcherByNameResponse_QNAME = new QName("http://ws/", "getResearcherByNameResponse");
    private static final QName _GetResearchersByInterest_QNAME = new QName("http://ws/", "getResearchersByInterest");
    private static final QName _GetResearchersByInterestResponse_QNAME = new QName("http://ws/", "getResearchersByInterestResponse");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: soap
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link GetAllResearchers }
     * 
     * @return
     *     the new instance of {@link GetAllResearchers }
     */
    public GetAllResearchers createGetAllResearchers() {
        return new GetAllResearchers();
    }

    /**
     * Create an instance of {@link GetAllResearchersResponse }
     * 
     * @return
     *     the new instance of {@link GetAllResearchersResponse }
     */
    public GetAllResearchersResponse createGetAllResearchersResponse() {
        return new GetAllResearchersResponse();
    }

    /**
     * Create an instance of {@link GetResearcherByName }
     * 
     * @return
     *     the new instance of {@link GetResearcherByName }
     */
    public GetResearcherByName createGetResearcherByName() {
        return new GetResearcherByName();
    }

    /**
     * Create an instance of {@link GetResearcherByNameResponse }
     * 
     * @return
     *     the new instance of {@link GetResearcherByNameResponse }
     */
    public GetResearcherByNameResponse createGetResearcherByNameResponse() {
        return new GetResearcherByNameResponse();
    }

    /**
     * Create an instance of {@link GetResearchersByInterest }
     * 
     * @return
     *     the new instance of {@link GetResearchersByInterest }
     */
    public GetResearchersByInterest createGetResearchersByInterest() {
        return new GetResearchersByInterest();
    }

    /**
     * Create an instance of {@link GetResearchersByInterestResponse }
     * 
     * @return
     *     the new instance of {@link GetResearchersByInterestResponse }
     */
    public GetResearchersByInterestResponse createGetResearchersByInterestResponse() {
        return new GetResearchersByInterestResponse();
    }

    /**
     * Create an instance of {@link Researcher }
     * 
     * @return
     *     the new instance of {@link Researcher }
     */
    public Researcher createResearcher() {
        return new Researcher();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetAllResearchers }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link GetAllResearchers }{@code >}
     */
    @XmlElementDecl(namespace = "http://ws/", name = "getAllResearchers")
    public JAXBElement<GetAllResearchers> createGetAllResearchers(GetAllResearchers value) {
        return new JAXBElement<>(_GetAllResearchers_QNAME, GetAllResearchers.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetAllResearchersResponse }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link GetAllResearchersResponse }{@code >}
     */
    @XmlElementDecl(namespace = "http://ws/", name = "getAllResearchersResponse")
    public JAXBElement<GetAllResearchersResponse> createGetAllResearchersResponse(GetAllResearchersResponse value) {
        return new JAXBElement<>(_GetAllResearchersResponse_QNAME, GetAllResearchersResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetResearcherByName }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link GetResearcherByName }{@code >}
     */
    @XmlElementDecl(namespace = "http://ws/", name = "getResearcherByName")
    public JAXBElement<GetResearcherByName> createGetResearcherByName(GetResearcherByName value) {
        return new JAXBElement<>(_GetResearcherByName_QNAME, GetResearcherByName.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetResearcherByNameResponse }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link GetResearcherByNameResponse }{@code >}
     */
    @XmlElementDecl(namespace = "http://ws/", name = "getResearcherByNameResponse")
    public JAXBElement<GetResearcherByNameResponse> createGetResearcherByNameResponse(GetResearcherByNameResponse value) {
        return new JAXBElement<>(_GetResearcherByNameResponse_QNAME, GetResearcherByNameResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetResearchersByInterest }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link GetResearchersByInterest }{@code >}
     */
    @XmlElementDecl(namespace = "http://ws/", name = "getResearchersByInterest")
    public JAXBElement<GetResearchersByInterest> createGetResearchersByInterest(GetResearchersByInterest value) {
        return new JAXBElement<>(_GetResearchersByInterest_QNAME, GetResearchersByInterest.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetResearchersByInterestResponse }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link GetResearchersByInterestResponse }{@code >}
     */
    @XmlElementDecl(namespace = "http://ws/", name = "getResearchersByInterestResponse")
    public JAXBElement<GetResearchersByInterestResponse> createGetResearchersByInterestResponse(GetResearchersByInterestResponse value) {
        return new JAXBElement<>(_GetResearchersByInterestResponse_QNAME, GetResearchersByInterestResponse.class, null, value);
    }

}
