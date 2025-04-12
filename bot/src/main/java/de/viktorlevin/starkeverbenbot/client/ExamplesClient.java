package de.viktorlevin.starkeverbenbot.client;

import de.viktorlevin.starkeverbenbot.dto.ExamplesDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "examples-client", url = "${examples.url}")
public interface ExamplesClient {
    @GetMapping("/examples/one")
    ExamplesDto getExample(@RequestParam String word);
}