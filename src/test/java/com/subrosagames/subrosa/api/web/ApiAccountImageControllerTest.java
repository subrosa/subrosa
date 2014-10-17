package com.subrosagames.subrosa.api.web;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.junit.Ignore;
import org.junit.Test;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.web.servlet.MvcResult;

import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.google.common.io.CharStreams;

import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.fileUpload;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import static com.subrosagames.subrosa.test.matchers.IsNotificationList.notificationList;
import static com.subrosagames.subrosa.test.matchers.IsPaginatedList.paginatedList;
import static com.subrosagames.subrosa.test.matchers.IsPaginatedListWithResultCount.hasResultCount;
import static com.subrosagames.subrosa.test.matchers.IsPaginatedListWithResultsSize.hasResultsSize;
import static com.subrosagames.subrosa.test.matchers.NotificationListHas.NotificationDetail.withDetail;
import static com.subrosagames.subrosa.test.matchers.NotificationListHas.hasNotification;

/**
 * Test {@link ApiAccountImageControllerTest}.
 */
@TestExecutionListeners(DbUnitTestExecutionListener.class)
@DatabaseSetup("/fixtures/accounts.xml")
public class ApiAccountImageControllerTest extends AbstractApiControllerTest {

    // CHECKSTYLE-OFF: JavadocMethod

    @Test
    public void testUploadImage() throws Exception {
        mockMvc.perform(
                fileUpload("/account/{accountId}/image", 1)
                        .file(getMock1x1Gif())
                        .with(user("bob@user.com")))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("1x1.gif"))
                .andExpect(jsonPath("$.mimeType").value("image/gif"))
                .andExpect(jsonPath("$.size").value(42))
                .andExpect(header().string("Location", "/account/1/image/50"));
    }

    @Test
    public void testUploadImageUnauthenticated() throws Exception {
        mockMvc.perform(
                fileUpload("/account/{accountId}/image", 1)
                        .file(getMock1x1Gif()))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void testUploadImageAccountNotFound() throws Exception {
        mockMvc.perform(
                fileUpload("/account/{accountId}/image", 666)
                        .file(getMock1x1Gif())
                        .with(user("bob@user.com")))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testUploadImageToWrongAccountForbidden() throws Exception {
        mockMvc.perform(
                fileUpload("/account/{accountId}/image", 2)
                        .file(getMock1x1Gif())
                        .with(user("bob@user.com")))
                .andExpect(status().isForbidden());
    }

    @Test
    public void testListImages() throws Exception {
        mockMvc.perform(
                get("/account/{accountId}/image", 3)
                        .with(user("lotsopics@user.com")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(is(paginatedList())))
                .andExpect(jsonPath("$").value(hasResultCount(3)))
                .andExpect(jsonPath("$.results[*].name").value(contains("pic1.png", "pic2.png", "pic3.png")));
    }

    @Test
    public void testListImagesLimitAndOffset() throws Exception {
        mockMvc.perform(
                get("/account/{accountId}/image", 3)
                        .param("limit", "1")
                        .param("offset", "1")
                        .with(user("lotsopics@user.com")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(is(paginatedList())))
                .andExpect(jsonPath("$").value(hasResultCount(3)))
                .andExpect(jsonPath("$").value(hasResultsSize(1)))
                .andExpect(jsonPath("$.results[0].name").value("pic2.png"));
    }

    @Test
    public void testListImagesEmpty() throws Exception {
        mockMvc.perform(
                get("/account/{accountId}/image", 1)
                        .with(user("bob@user.com")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(is(paginatedList())))
                .andExpect(jsonPath("$").value(hasResultCount(0)));
    }

    @Test
    public void testListImagesUnauthenticated() throws Exception {
        mockMvc.perform(
                get("/account/{accountId}/image", 1))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void testListImagesForWrongAccountForbidden() throws Exception {
        mockMvc.perform(
                get("/account/{accountId}/image", 2)
                        .with(user("bob@user.com")))
                .andExpect(status().isForbidden());
    }

    @Test
    public void testRetrieveImage() throws Exception {
        mockMvc.perform(
                get("/account/{accountId}/image/{imageId}", 3, 1)
                        .with(user("lotsopics@user.com")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("pic1.png"))
                .andExpect(jsonPath("$.size").value(100))
                .andExpect(jsonPath("$.mimeType").value("image/png"));
    }

    @Test
    public void testRetrieveImageUnauthenticated() throws Exception {
        mockMvc.perform(
                get("/account/{accountId}/image/{imageId}", 3, 1))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void testRetrieveImageForWrongAccountForbidden() throws Exception {
        mockMvc.perform(
                get("/account/{accountId}/image/{imageId}", 3, 1)
                        .with(user("bob@user.com")))
                .andExpect(status().isForbidden());
    }

    @Ignore
    @Test
    public void testUploadImageUnrecognizedMimeType() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "pic.png", "image/png", "not a real image".getBytes("UTF-8"));
        mockMvc.perform(
                fileUpload("/account/{accountId}/image", 1)
                        .file(file)
                        .with(user("bob@user.com")))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$").value(notificationList()))
                .andExpect(jsonPath("$.notifications").value(hasNotification(withDetail("file", "unsupportedType"))));
    }

    @Ignore
    @Test
    public void testDownloadImage() throws Exception {
        MvcResult mvcResult = mockMvc.perform(
                get("/account/{accountId}/image/{size}.{format}", 1, "1x1", "gif")
                        .with(user("bob@user.com")))
                .andExpect(status().isOk())
                .andReturn();
        String gifString = CharStreams.toString(new InputStreamReader(getMock1x1Gif().getInputStream()));
        assertEquals(gifString, mvcResult.getResponse().getContentAsString());
    }

    @Ignore
    @Test
    public void testDownloadImageNotAuthenticated() throws Exception {
        mockMvc.perform(
                get("/account/{accountId}/image/{size}.{format}", 1, "1x1", "gif"))
                .andExpect(status().isUnauthorized());
    }

    @Ignore
    @Test
    public void testDownloadImageWrongAccountForbidden() throws Exception {
        mockMvc.perform(
                get("/account/{accountId}/image/{size}.{format}", 2, "1x1", "gif")
                        .with(user("bob@user.com")))
                .andExpect(status().isForbidden());
    }

    MockMultipartFile getMock1x1Gif() throws IOException {
        InputStream fileInputStream = ClassLoader.getSystemResourceAsStream("1x1.gif");
        return new MockMultipartFile("file", "1x1.gif", "image/gif", fileInputStream);
    }

    // CHECKSTYLE-ON: JavadocMethod
}