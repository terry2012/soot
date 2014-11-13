package soot.dexpler.instructions;

import java.util.List;

import org.jf.dexlib2.AccessFlags;
import org.jf.dexlib2.analysis.AnalyzedInstruction;
import org.jf.dexlib2.analysis.InlineMethodResolver;
import org.jf.dexlib2.dexbacked.DexBackedOdexFile;
import org.jf.dexlib2.iface.DexFile;
import org.jf.dexlib2.iface.Method;
import org.jf.dexlib2.iface.instruction.Instruction;
import org.jf.dexlib2.iface.instruction.formats.Instruction35mi;
import org.jf.dexlib2.iface.instruction.formats.Instruction3rmi;
import org.jf.dexlib2.iface.reference.MethodReference;

import soot.dexpler.DexBody;

public class ExecuteInlineInstruction extends MethodInvocationInstruction implements OdexInstruction {
	
	private Method targetMethod = null;

	public ExecuteInlineInstruction(Instruction instruction, int codeAddress) {
		super(instruction, codeAddress);
	}
	
	@Override
	public void deOdex(DexFile parentFile) {
		if (!(parentFile instanceof DexBackedOdexFile))
			throw new RuntimeException("ODEX instruction in non-ODEX file");
		DexBackedOdexFile odexFile = (DexBackedOdexFile) parentFile;
		InlineMethodResolver inlineMethodResolver = InlineMethodResolver.createInlineMethodResolver(
				odexFile.getOdexVersion());
		targetMethod = inlineMethodResolver.resolveExecuteInline(
				new AnalyzedInstruction(instruction, -1, -1));
	}
	
	@Override
    protected MethodReference getTargetMethodReference() {
		return targetMethod;
    }
    
	@Override
	public void jimplify(DexBody body) {
    	int acccessFlags = targetMethod.getAccessFlags();
        if (AccessFlags.STATIC.isSet(acccessFlags))
            jimplifyStatic();
        else if (AccessFlags.PRIVATE.isSet(acccessFlags))
            jimplifySpecial();
        else
            jimplifyVirtual();
	}
	
    /**
     * Return the indices used in this instruction.
     *
     * @return a list of register indices
     */
    protected List<Integer> getUsedRegistersNums() {
        if (instruction instanceof Instruction35mi)
            return getUsedRegistersNums((Instruction35mi) instruction);
        else if (instruction instanceof Instruction3rmi)
            return getUsedRegistersNums((Instruction3rmi) instruction);
        throw new RuntimeException("Instruction is not an ExecuteInline");
    }
    
}