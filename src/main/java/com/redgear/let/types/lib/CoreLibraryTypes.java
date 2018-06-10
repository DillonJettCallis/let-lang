package com.redgear.let.types.lib;

import com.redgear.let.types.LibraryTypeScope;
import com.redgear.let.types.LiteralTypeToken;

public class CoreLibraryTypes {

    public void addTypes(LibraryTypeScope typeScope) {
        typeScope.declareType("print", LiteralTypeToken.stringTypeToken);
    }

}
