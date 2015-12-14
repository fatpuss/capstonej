/*
Copyright (c) 2015, Keve Müller
All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are met:

* Redistributions of source code must retain the above copyright notice, this
  list of conditions and the following disclaimer.

* Redistributions in binary form must reproduce the above copyright notice,
  this list of conditions and the following disclaimer in the documentation
  and/or other materials provided with the distribution.

* Neither the name of capstonej nor the names of its
  contributors may be used to endorse or promote products derived from
  this software without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/
package hu.keve.capstonej;

import java.util.Iterator;
import java.util.NoSuchElementException;

import org.bridj.Pointer;

import hu.keve.capstonebinding.CapstoneLibrary;
import hu.keve.capstonebinding.CapstoneLibrary.ppc_insn_group;
import hu.keve.capstonebinding.cs_detail;
import hu.keve.capstonebinding.cs_insn;

public final class CapstoneDisassembly implements Iterable<Pointer<cs_insn>> {
    private final Pointer<cs_insn> insnA;
    private final int count;

    public CapstoneDisassembly(final Pointer<cs_insn> insnA, final int count) {
        this.insnA = insnA;
        this.count = count;
    }

    public void close() {
        CapstoneLibrary.csFree(insnA, count);
    }

    public Pointer<cs_insn> getInsnP(final int idx) {
        return insnA.next(idx);
    }

    public cs_insn getInsn(final int idx) {
        return insnA.get(idx);
    }

    public int getCount() {
        return count;
    }

    public static boolean group(cs_insn insn, ppc_insn_group grp) {
        return group(insn, grp.value());
    }

    private static boolean group(cs_insn insn, long grp) {
        cs_detail id = insn.detail().get();
        for (int i = 0; i < id.groups_count(); i++) {
            if (grp == id.groups().get(i)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public Iterator<Pointer<cs_insn>> iterator() {
        return new Iterator<Pointer<cs_insn>>() {
            int idx = 0;

            @Override
            public boolean hasNext() {
                return idx < count;
            }

            @Override
            public Pointer<cs_insn> next() {
                if (idx >= count) {
                    throw new NoSuchElementException();
                }
                return getInsnP(idx++);
            }
        };
    }
}