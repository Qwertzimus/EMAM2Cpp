package de.monticore.lang.monticar.generator.order.nfp.TagMinMaxTagSchema;

import de.monticore.CommonModelingLanguage;
import de.monticore.lang.montiarc.tagging._symboltable.TagableModelingLanguage;
import de.monticore.symboltable.resolving.CommonResolvingFilter;

public class TagMinMaxTagSchema {

  protected static TagMinMaxTagSchema instance = null;

  protected TagMinMaxTagSchema() {}

  protected static TagMinMaxTagSchema getInstance() {
    if (instance == null) {
      instance = new TagMinMaxTagSchema();
    }
    return instance;
  }

  protected void doRegisterTagTypes(TagableModelingLanguage modelingLanguage) {
    // all ModelingLanguage instances are actually instances of CommonModelingLanguage
    if(modelingLanguage instanceof CommonModelingLanguage) {
      CommonModelingLanguage commonModelingLanguage = (CommonModelingLanguage)modelingLanguage;

      modelingLanguage.addTagSymbolCreator(new TagMinMaxSymbolCreator());
      commonModelingLanguage.addResolver(CommonResolvingFilter.create(TagMinMaxSymbol.KIND));
    }
  }

  public static void registerTagTypes(TagableModelingLanguage modelingLanguage) {
    getInstance().doRegisterTagTypes(modelingLanguage);
  }
}