package com.ntnu.backend.controller;

import com.ntnu.backend.model.CodeModel;
import com.ntnu.backend.service.CompilerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * Controller class responsible for handling requests to the /compiler endpoint
 */
@RestController
@CrossOrigin(origins = "http://localhost:8080")
public class CompilerController {

  @Autowired
  CompilerService compilerService;

  @GetMapping("/")
  public String test(){
    return "Hello World";
  }
  @PostMapping("/compiler")
  public CodeModel compileCode(@RequestBody CodeModel codeModel) {
    return compilerService.compileCode(codeModel);
  }
}
