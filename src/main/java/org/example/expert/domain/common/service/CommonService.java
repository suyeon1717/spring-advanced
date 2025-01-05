package org.example.expert.domain.common.service;

import org.example.expert.domain.common.exception.InvalidRequestException;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

@Service
public class CommonService {

    public CommonService() {

    }

    public <T> T findEntityById(JpaRepository<T, Long> repository, Long id, String errorMessage) {

        return repository.findById(id).orElseThrow(() -> new InvalidRequestException(errorMessage));

    }
}
