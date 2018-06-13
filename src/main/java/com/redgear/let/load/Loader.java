package com.redgear.let.load;

import com.redgear.let.ast.Module;

public interface Loader {
    Module loadModule(String moduleName);
}
