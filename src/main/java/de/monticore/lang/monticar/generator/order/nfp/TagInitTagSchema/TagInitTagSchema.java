package de.monticore.lang.monticar.generator.order.nfp.TagInitTagSchema;

import de.monticore.CommonModelingLanguage;
import de.monticore.lang.montiarc.tagging._symboltable.TagableModelingLanguage;
import de.monticore.symboltable.resolving.CommonResolvingFilter;

public class TagInitTagSchema {

  protected static TagInitTagSchema instance = null;

  protected TagInitTagSchema() {}

  protected static TagInitTagSchema getInstance() {
    if (instance == null) {
      instance = new TagInitTagSchema();
    }
    return instance;
  }

  protected void doRegisterTagTypes(TagableModelingLanguage modelingLanguage) {
    // all ModelingLanguage instances are actually instances of CommonModelingLanguage
    if(modelingLanguage instanceof CommonModelingLanguage) {
      CommonModelingLanguage commonModelingLanguage = (CommonModelingLanguage)modelingLanguage;

      modelingLanguage.addTagSymbolCreator(new TagInitSymbolCreator());
      commonModelingLanguage.addResolver(CommonResolvingFilter.create(TagInitSymbol.KIND));
    }
  }

  public static void registerTagTypes(TagableModelingLanguage modelingLanguage) {
    getInstance().doRegisterTagTypes(modelingLanguage);
  }
}