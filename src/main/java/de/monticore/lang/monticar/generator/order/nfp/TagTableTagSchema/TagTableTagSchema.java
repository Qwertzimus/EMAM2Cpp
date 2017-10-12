package de.monticore.lang.monticar.generator.order.nfp.TagTableTagSchema;

import de.monticore.CommonModelingLanguage;
import de.monticore.lang.montiarc.tagging._symboltable.TagableModelingLanguage;
import de.monticore.symboltable.resolving.CommonResolvingFilter;

public class TagTableTagSchema {

  protected static TagTableTagSchema instance = null;

  protected TagTableTagSchema() {}

  protected static TagTableTagSchema getInstance() {
    if (instance == null) {
      instance = new TagTableTagSchema();
    }
    return instance;
  }

  protected void doRegisterTagTypes(TagableModelingLanguage modelingLanguage) {
    // all ModelingLanguage instances are actually instances of CommonModelingLanguage
    if(modelingLanguage instanceof CommonModelingLanguage) {
      CommonModelingLanguage commonModelingLanguage = (CommonModelingLanguage)modelingLanguage;

      modelingLanguage.addTagSymbolCreator(new TagTableSymbolCreator());
      commonModelingLanguage.addResolver(CommonResolvingFilter.create(TagTableSymbol.KIND));
    }
  }

  public static void registerTagTypes(TagableModelingLanguage modelingLanguage) {
    getInstance().doRegisterTagTypes(modelingLanguage);
  }
}