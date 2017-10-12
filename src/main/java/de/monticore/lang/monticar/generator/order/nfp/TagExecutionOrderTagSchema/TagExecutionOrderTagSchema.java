package de.monticore.lang.monticar.generator.order.nfp.TagExecutionOrderTagSchema;

import de.monticore.CommonModelingLanguage;
import de.monticore.lang.montiarc.tagging._symboltable.TagableModelingLanguage;
import de.monticore.symboltable.resolving.CommonResolvingFilter;
import de.se_rwth.commons.logging.Log;

public class TagExecutionOrderTagSchema {

  protected static TagExecutionOrderTagSchema instance = null;

  protected TagExecutionOrderTagSchema() {}

  protected static TagExecutionOrderTagSchema getInstance() {
    if (instance == null) {
      instance = new TagExecutionOrderTagSchema();
    }
    return instance;
  }

  protected void doRegisterTagTypes(TagableModelingLanguage modelingLanguage) {
    // all ModelingLanguage instances are actually instances of CommonModelingLanguage
    if(modelingLanguage instanceof CommonModelingLanguage) {
      CommonModelingLanguage commonModelingLanguage = (CommonModelingLanguage)modelingLanguage;

      modelingLanguage.addTagSymbolCreator(new TagExecutionOrderSymbolCreator());
      commonModelingLanguage.addResolver(CommonResolvingFilter.create(TagExecutionOrderSymbol.KIND));
    }else{
      Log.error("Not taggable");
    }
  }

  public static void registerTagTypes(TagableModelingLanguage modelingLanguage) {
    getInstance().doRegisterTagTypes(modelingLanguage);
  }
}