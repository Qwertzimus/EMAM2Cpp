package de.monticore.lang.monticar.generator.order.nfp.TagThresholdTagSchema;

import de.monticore.CommonModelingLanguage;
import de.monticore.lang.montiarc.tagging._symboltable.TagableModelingLanguage;
import de.monticore.symboltable.resolving.CommonResolvingFilter;

public class TagThresholdTagSchema {

  protected static TagThresholdTagSchema instance = null;

  protected TagThresholdTagSchema() {}

  protected static TagThresholdTagSchema getInstance() {
    if (instance == null) {
      instance = new TagThresholdTagSchema();
    }
    return instance;
  }

  protected void doRegisterTagTypes(TagableModelingLanguage modelingLanguage) {
    // all ModelingLanguage instances are actually instances of CommonModelingLanguage
    if(modelingLanguage instanceof CommonModelingLanguage) {
      CommonModelingLanguage commonModelingLanguage = (CommonModelingLanguage)modelingLanguage;

      modelingLanguage.addTagSymbolCreator(new TagThresholdSymbolCreator());
      commonModelingLanguage.addResolver(CommonResolvingFilter.create(TagThresholdSymbol.KIND));
    }
  }

  public static void registerTagTypes(TagableModelingLanguage modelingLanguage) {
    getInstance().doRegisterTagTypes(modelingLanguage);
  }
}