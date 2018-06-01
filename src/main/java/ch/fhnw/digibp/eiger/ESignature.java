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
import java.io.InputStream;

import org.apache.commons.io.IOUtils;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;

public class ESignature implements JavaDelegate {

	// create a byte array that will hold our document bytes
	byte[] fileBytes = null;

	//String pathToDocument = "classpath:pdf-sample.pdf";

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
			InputStream in = this.getClass().getClassLoader().getResourceAsStream("Employment_Agreement1.pdf");
			//Path path = Paths.get(pathToDocument);
			//fileBytes = Files.readAllBytes(path);
			fileBytes = IOUtils.toByteArray(in);
		}
		catch (IOException ioExcp)
		{
			// TODO: handle error
			System.out.println("Exception: " + ioExcp);
			return;
		}

		// create an envelope that will store the document(s), field(s), and recipient(s)
		EnvelopeDefinition envDef = new EnvelopeDefinition();
		envDef.setEmailSubject("DigiBP Eiger Employment Contract - please sign");

		// add a document to the envelope
		Document doc = new Document();  
		String base64Doc = Base64.getEncoder().encodeToString(fileBytes);
		doc.setDocumentBase64(base64Doc);
		doc.setName("EmploymentContract"); // can be different from actual file name
		doc.setFileExtension(".pdf"); // update if different extension!
		doc.setDocumentId("1");

		List<Document> docs = new ArrayList<Document>();
		docs.add(doc);
		envDef.setDocuments(docs);

		int yPos = 427;
		int xPos = 80;
		// add a recipient to sign the document, identified by name, email, and recipientId
		Signer signer1 = new Signer();
		signer1.setEmail("recruitment.digibpeiger@gmail.com");//put email details 
		signer1.setName("Director of Human Resources, DigiBP Eiger");
		signer1.setRecipientId("1");
		signer1.setRoutingOrder("1");

		// create a signHere tab somewhere on the document for the signer to sign
		// default unit of measurement is pixels, can be mms, cms, inches also
		SignHere signHere1 = new SignHere();
		signHere1.setDocumentId("1");
		signHere1.setPageNumber("3");
		signHere1.setRecipientId("1");
		signHere1.setXPosition(""+xPos);
		signHere1.setYPosition(""+yPos);
		

		// can have multiple tabs, so need to add to envelope as a single element list
		List<SignHere> signHereTabs1 = new ArrayList<SignHere>();
		signHereTabs1.add(signHere1);

		Tabs tabs1 = new Tabs();
		tabs1.setSignHereTabs(signHereTabs1);
		signer1.setTabs(tabs1);

		//Signer 2
		yPos = 316;
		
		Signer signer2 = new Signer();
		String candidateEmail = execution.getVariable("finalCandidateId").toString();
		System.out.println("Final candidate id: " + candidateEmail);
		signer2.setEmail(candidateEmail);//put email details 
		signer2.setName("Employee");
		signer2.setRecipientId("2");
		signer2.setRoutingOrder("2");

		SignHere signHere2 = new SignHere();
		signHere2.setDocumentId("1");
		signHere2.setPageNumber("3");
		signHere2.setRecipientId("2");
		signHere2.setXPosition(""+xPos);
		signHere2.setYPosition(""+yPos);
		
		List<SignHere> signHereTabs2 = new ArrayList<SignHere>();
		signHereTabs2.add(signHere2);

		Tabs tabs2 = new Tabs();
		tabs2.setSignHereTabs(signHereTabs2);
		signer2.setTabs(tabs2);


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