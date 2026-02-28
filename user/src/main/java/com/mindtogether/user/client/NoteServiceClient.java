package com.mindtogether.user.client;

import com.mindtogether.user.model.service.dto.NoteDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import java.util.List;

@FeignClient(name = "notes-service", configuration=FeignSecurityConfig.class)
public interface NoteServiceClient {

    @GetMapping("/api/notes/user/{login}")
    List<NoteDTO> findForUser(@PathVariable String login);

    @DeleteMapping("/api/notes/user/{login}")
    List<NoteDTO> deleteForUser(@PathVariable String login);

}

