package com.redgear.let.eval;

/**
 * Created by LordBlackHole on 2017-01-08.
 */
public interface ModuleDefinition {

    String getName();

    void buildLibrary(ModuleScope moduleScope);


}
