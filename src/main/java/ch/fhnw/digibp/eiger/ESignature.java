package ch.fhnw.digibp.eiger;

//DocuSign imports
import com.docusign.esign.api.*;
import com.docusign.esign.client.*;
import com.docusign.esign.model.*;

//Additional imports
import java.util.List;

import java.util.ArrayList;
import java.util.Base64;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.io.IOException;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;

public class ESignature implements JavaDelegate {

	// create a byte array that will hold our document bytes
	byte[] fileBytes = null;

	String pathToDocument = "resources/static/pdf-sample.pdf";

	public void execute(DelegateExecution execution) throws Exception {
		//Authentication starts
		String UserName = "recruitment.digibpeiger@gmail.com";
		String Password = "digibp@eiger";   
		String IntegratorKey = "2ea2345e-3b48-44f0-9d68-9fb991e1390a";

		// for production environment update to "www.docusign.net/restapi"
		String BaseUrl = "https://demo.docusign.net/restapi";

		// initialize the api client for the desired environment
		ApiClient apiClient = new ApiClient();

		apiClient.setBasePath(BaseUrl);

		// create JSON formatted auth header
		String creds = "{\"Username\":\"" +  UserName + "\",\"Password\":\"" +  Password + "\",\"IntegratorKey\":\"" +  IntegratorKey + "\"}";
		apiClient.addDefaultHeader("X-DocuSign-Authentication", creds);

		// assign api client to the Configuration object
		Configuration.setDefaultApiClient(apiClient);

		// create an empty list that we will populate with account(s)
		List<LoginAccount> loginAccounts = null;	

		try
		{
			// login call available off the AuthenticationApi
			AuthenticationApi authApi = new AuthenticationApi();

			// login has some optional parameters we can set
			AuthenticationApi.LoginOptions loginOps = authApi.new LoginOptions();
			loginOps.setApiPassword("true");
			loginOps.setIncludeAccountIdGuid("true");
			LoginInformation loginInfo = authApi.login(loginOps);

			// note that a given user may be a member of multiple accounts
			loginAccounts = loginInfo.getLoginAccounts();

			System.out.println("LoginInformation: " + loginAccounts);
		}
		catch (com.docusign.esign.client.ApiException ex)
		{
			System.out.println("Exception: " + ex);
		}
		catch(Exception e) {
			System.out.println("Exception: "+e);
		}
		//Authentication ends

		//Signing starts	
		try
		{
			String currentDir = System.getProperty("user.dir");
			System.out.println(currentDir);
			// read file from a local directory
			Path path = Paths.get(pathToDocument);
			fileBytes = Files.readAllBytes(path);
		}
		catch (IOException ioExcp)
		{
			// TODO: handle error
			System.out.println("Exception: " + ioExcp);
			return;
		}

		// create an envelope that will store the document(s), field(s), and recipient(s)
		EnvelopeDefinition envDef = new EnvelopeDefinition();
		envDef.setEmailSubject("Employment Contract - please sign");

		// add a document to the envelope
		Document doc = new Document();  
		String base64Doc = Base64.getEncoder().encodeToString(fileBytes);
		doc.setDocumentBase64(base64Doc);
		doc.setName("TestFile"); // can be different from actual file name
		doc.setFileExtension(".pdf"); // update if different extension!
		doc.setDocumentId("1");

		List<Document> docs = new ArrayList<Document>();
		docs.add(doc);
		envDef.setDocuments(docs);

		int yPos = 50;
		int xPos = 15;
		// add a recipient to sign the document, identified by name, email, and recipientId
		Signer signer1 = new Signer();
		signer1.setEmail("recruitment.digibpeiger@gmail.com");//put email details 
		signer1.setName("DigiBP Eiger - HR Manager");
		signer1.setRecipientId("1");
		signer1.setRoutingOrder("1");

		// create a signHere tab somewhere on the document for the signer to sign
		// default unit of measurement is pixels, can be mms, cms, inches also
		SignHere signHere = new SignHere();
		signHere.setDocumentId("1");
		signHere.setPageNumber("1");
		signHere.setRecipientId("1");
		signHere.setXPosition(""+xPos);
		signHere.setYPosition(""+yPos);
		yPos+=50;

		// can have multiple tabs, so need to add to envelope as a single element list
		List<SignHere> signHereTabs = new ArrayList<SignHere>();
		signHereTabs.add(signHere);

		Tabs tabs = new Tabs();
		tabs.setSignHereTabs(signHereTabs);
		signer1.setTabs(tabs);

		//Signer 2
		Signer signer2 = new Signer();
		signer2.setEmail("charutapande@yahoo.co.in");//put email details 
		signer2.setName("Employee");
		signer2.setRecipientId("2");
		signer2.setRoutingOrder("2");

		signHere = new SignHere();
		signHere.setDocumentId("1");
		signHere.setPageNumber("1");
		signHere.setRecipientId("1");
		signHere.setXPosition(""+xPos);
		signHere.setYPosition(""+yPos);

		signHereTabs.add(signHere);

		// add recipients (in this case a single signer) to the envelope
		envDef.setRecipients(new Recipients());
		envDef.getRecipients().setSigners(new ArrayList<Signer>());
		envDef.getRecipients().getSigners().add(signer1);
		envDef.getRecipients().getSigners().add(signer2);

		// send the envelope by setting |status| to "sent". To save as a draft set to "created" instead
		envDef.setStatus("sent");

		try
		{
			// instantiate a new EnvelopesApi object
			EnvelopesApi envelopesApi = new EnvelopesApi();

			// use the |accountId| we retrieved through the Login API to create the Envelope
			String accountId = loginAccounts.get(0).getAccountId();

			// call the createEnvelope() API
			EnvelopeSummary envelopeSummary = envelopesApi.createEnvelope(accountId, envDef);
			System.out.println("EnvelopeSummary: " + envelopeSummary);
		}
		catch (com.docusign.esign.client.ApiException ex)
		{
			System.out.println("Exception: " + ex);
		}
	}
	//Signing ends

} // end class