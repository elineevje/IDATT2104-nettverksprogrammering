package com.ntnu.backend.model;

/**
 * Model class for the code to be compiled
 */
public class CodeModel {

  String code;
  String compiledCode;

  public CodeModel() {
  }

  public String getCode() {
    return code;
  }


  public String getCompiledCode() {
    return compiledCode;
  }

  public void setCode(String code) {
    this.code = code;
  }


  public void setCompiledCode(String compiledCode) {
    this.compiledCode = compiledCode;
  }
}