package de.viktorlevin.starkeverbenbot.service;

import de.viktorlevin.starkeverbenbot.entity.StarkesVerb;
import de.viktorlevin.starkeverbenbot.repository.StarkesVerbRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class StarkeVerbenService {
    private final StarkesVerbRepository starkesVerbRepository;


    @Transactional(readOnly = true)
    public StarkesVerb getRandomStarkesVerb() {
        return starkesVerbRepository.getRandomStarkesVerb();
    }

}
