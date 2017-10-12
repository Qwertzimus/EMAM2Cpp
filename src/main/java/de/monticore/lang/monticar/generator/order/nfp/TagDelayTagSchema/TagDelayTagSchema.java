package de.monticore.lang.monticar.generator.order.nfp.TagDelayTagSchema;

import de.monticore.CommonModelingLanguage;
import de.monticore.lang.montiarc.tagging._symboltable.TagableModelingLanguage;
import de.monticore.symboltable.resolving.CommonResolvingFilter;

public class TagDelayTagSchema {

  protected static TagDelayTagSchema instance = null;

  protected TagDelayTagSchema() {}

  protected static TagDelayTagSchema getInstance() {
    if (instance == null) {
      instance = new TagDelayTagSchema();
    }
    return instance;
  }

  protected void doRegisterTagTypes(TagableModelingLanguage modelingLanguage) {
    // all ModelingLanguage instances are actually instances of CommonModelingLanguage
    if(modelingLanguage instanceof CommonModelingLanguage) {
      CommonModelingLanguage commonModelingLanguage = (CommonModelingLanguage)modelingLanguage;

      modelingLanguage.addTagSymbolCreator(new TagDelaySymbolCreator());
      commonModelingLanguage.addResolver(CommonResolvingFilter.create(TagDelaySymbol.KIND));
    }
  }

  public static void registerTagTypes(TagableModelingLanguage modelingLanguage) {
    getInstance().doRegisterTagTypes(modelingLanguage);
  }
}