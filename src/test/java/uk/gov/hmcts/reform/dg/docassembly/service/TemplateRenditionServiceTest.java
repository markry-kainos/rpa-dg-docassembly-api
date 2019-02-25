package uk.gov.hmcts.reform.dg.docassembly.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.OkHttpClient;
import okhttp3.mock.ClasspathResources;
import okhttp3.mock.MockInterceptor;
import okhttp3.mock.Rule;
import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Test;
import uk.gov.hmcts.reform.dg.docassembly.dto.CreateTemplateRenditionDto;
import uk.gov.hmcts.reform.dg.docassembly.dto.RenditionOutputType;
import uk.gov.hmcts.reform.dg.docassembly.dto.TemplateRenditionOutputDto;

import java.util.Base64;

public class TemplateRenditionServiceTest {

    MockInterceptor interceptor = new MockInterceptor();

    TemplateRenditionService templateRenditionService;

    @Before
    public void setup() {

        interceptor.reset();

        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(interceptor)
                .build();

        templateRenditionService = new TemplateRenditionService(
                client,
                "http://tonrnado.com",
                "x");
    }


    @Test
    public void testRendition() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();

        CreateTemplateRenditionDto createTemplateRenditionDto = new CreateTemplateRenditionDto();

        createTemplateRenditionDto.setJwt("x");
        createTemplateRenditionDto.setTemplateId(new String(Base64.getEncoder().encode("1".getBytes())));
        createTemplateRenditionDto.setOutputType(RenditionOutputType.PDF);
        createTemplateRenditionDto.setFormPayload(objectMapper.readTree("{}"));

        interceptor.addRule(new Rule.Builder()
                .post()
                .respond(ClasspathResources.resource("template1.docx")));

        TemplateRenditionOutputDto templateRenditionOutputDto =
                templateRenditionService.renderTemplate(createTemplateRenditionDto);

        IOUtils.contentEquals(templateRenditionOutputDto.getRendition(),
                ClasspathResources.resource("template1.docx"));
    }

}