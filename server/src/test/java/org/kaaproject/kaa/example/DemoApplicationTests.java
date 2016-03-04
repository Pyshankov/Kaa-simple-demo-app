package org.kaaproject.kaa.example;


import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kaaproject.kaa.example.controller.AnalyticController;
import org.kaaproject.kaa.example.service.KaaAdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import java.io.IOException;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = DemoApplication.class)
@WebAppConfiguration
public class DemoApplicationTests {

	@Autowired
	KaaAdminService adminService;

	static String validJson;

	static String invalidJson;

	@BeforeClass
	public static void setup() {
		validJson="{\"timestamp\":{\"long\":1455708552774},\"data\":{\"bytes\":\"Inter-|   Receive \"},\"endpointKeyHash\":{\"string\":\"fo6axznpiJgAZc3/C+03iOqiNUk=\\n\"},\"hashFunction\":{\"string\":\"SHA1\"}}";
		invalidJson= "{\"timestam\":{\"long\":1455706139264},\"data\":{\"bytes\":\"«\\u001C~\u0000ì)º0\\u001FðÔyáßT\u0000\"},\"endpointHashKey\":{\"string\":\"fo6axznpiJgAZc3/C+03iOqiNUk=\"},\"hashFunction\":{\"string\":\"MD5\"}}";
	}

	@Test
	public void sendNotification_validJsonGet() throws IOException{
		adminService.sendNotification(validJson);
	}

	@Test(expected = IOException.class)
	public void sendNotification_invalidJsonGet() throws IOException{
		adminService.sendNotification(invalidJson);
	}

	@Test
	public void encryptFile_PostTest()throws Exception {
		AnalyticController controller = new AnalyticController(adminService);
		MockMvc mockMvc = standaloneSetup(controller).build();
		mockMvc.perform(post("/encrypt")
				.content(validJson)
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk());
	}

	@Test(expected = Exception.class)
	public void encryptFile_PostTestWithInvalidJson()throws Exception {
		AnalyticController controller = new AnalyticController(adminService);
		MockMvc mockMvc = standaloneSetup(controller).build();
		mockMvc.perform(post("/encrypt")
				.content(invalidJson)
				.contentType(MediaType.APPLICATION_JSON));
	}




}
