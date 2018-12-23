package com.github.ysl3000.quantum.impl.utils;

import org.bukkit.Material;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by ysl3000 on 19.01.17.
 */
public class Normalizer {
    public static final Normalizer NORMALIZER = new Normalizer();
    public final Replacer<Enum, String> materialNormalizer = new MaterialNormalizer();
    public final Replacer<Material, String> materialStringReplacer = new MaterialName();

    private Normalizer() {
    }

    public Set<String> normalizeEnumNames(Set<? extends Enum> enums, Replacer<Enum, String> replacer) {
        Set<String> names = new HashSet<>();
        for (Enum material : enums)
            names.add(replacer.replace(material));
        return names;
    }

    private class MaterialNormalizer implements Replacer<Enum, String> {
        @Override
        public String replace(Enum in) {
            return in.name().toLowerCase().replace("_", " ");
        }
    }

    private class MaterialName implements Replacer<Material, String> {
        @Override
        public String replace(Material in) {
            return in.name();
        }
    }


}
