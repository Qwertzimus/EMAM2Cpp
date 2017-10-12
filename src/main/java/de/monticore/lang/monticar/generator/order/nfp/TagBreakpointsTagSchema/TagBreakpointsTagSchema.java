package de.monticore.lang.monticar.generator.order.nfp.TagBreakpointsTagSchema;

import de.monticore.CommonModelingLanguage;
import de.monticore.lang.montiarc.tagging._symboltable.TagableModelingLanguage;
import de.monticore.symboltable.resolving.CommonResolvingFilter;

public class TagBreakpointsTagSchema {

  protected static TagBreakpointsTagSchema instance = null;

  protected TagBreakpointsTagSchema() {}

  protected static TagBreakpointsTagSchema getInstance() {
    if (instance == null) {
      instance = new TagBreakpointsTagSchema();
    }
    return instance;
  }

  protected void doRegisterTagTypes(TagableModelingLanguage modelingLanguage) {
    // all ModelingLanguage instances are actually instances of CommonModelingLanguage
    if(modelingLanguage instanceof CommonModelingLanguage) {
      CommonModelingLanguage commonModelingLanguage = (CommonModelingLanguage)modelingLanguage;

      modelingLanguage.addTagSymbolCreator(new TagBreakpointsSymbolCreator());
      commonModelingLanguage.addResolver(CommonResolvingFilter.create(TagBreakpointsSymbol.KIND));
    }
  }

  public static void registerTagTypes(TagableModelingLanguage modelingLanguage) {
    getInstance().doRegisterTagTypes(modelingLanguage);
  }
}