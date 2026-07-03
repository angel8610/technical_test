package com.example.demo.services;

import com.example.demo.vos.PetVO;

import java.util.Optional;

public interface PetExternalService {

  Optional<PetVO> findById(Long petId);

  PetVO save(PetVO petVO);


}
