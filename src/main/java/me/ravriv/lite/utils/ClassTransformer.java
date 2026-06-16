package me.ravriv.lite.utils;

import net.minecraft.launchwrapper.IClassTransformer;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;

public class ClassTransformer implements IClassTransformer {

    public static String optifineVersion = "NONE";

    public static void initOptiFine() {
        try {
            ClassNode classNode = new ClassNode();
            new ClassReader("Config").accept(classNode, ClassReader.SKIP_CODE);

            for (FieldNode field : classNode.fields) {
                if ("OF_RELEASE".equals(field.name)) {
                    optifineVersion = (String) field.value;
                    return;
                }
            }
        } catch (Throwable t) {
            optifineVersion = "NONE";
        }
    }

    @Override
    public byte[] transform(String name, String transformedName, byte[] bytes) {
        return bytes;
    }
}
