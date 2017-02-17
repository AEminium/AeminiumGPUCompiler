import os
import random
import sys

sys.setrecursionlimit(10000)
REC_TREE = 15

target_folder = "synthetic_src"
package_folder = "%s/%s" % (target_folder, "synthetic")

for folder in [target_folder, package_folder]:
    if not os.path.exists(folder):
        os.mkdir(folder)

types = ['Integer', 'Long', 'Float', 'Double']

source = lambda i, code : """
package synthetic;

import aeminium.gpu.collections.lazyness.*;
import aeminium.gpu.collections.lists.*;
import aeminium.gpu.operations.functions.*;

public class Synthetic%d {
    public static void main(String[] args) {
        for (int SIZE=10; SIZE <= 10000000; SIZE *= 10) {
            %s
            System.gc();
        }
    }
}
""" % (i, code)

def lower_type(t):
    if t == 'Integer':
        return 'int'
    if t == 'Long':
        return 'long'
    if t == 'Float':
        return 'float'
    if t == 'Double':
        return 'double'
        
def default_type(t):
    if t == 'Integer':
        return '0'
    if t == 'Long':
        return '0L'
    if t == 'Float':
        return '0.0f'
    if t == 'Double':
        return '0.0'
        
def get_var(t, ctx):
    random.shuffle(ctx)
    for k in ctx:
        if k[1] == t:
            return k[0]
    return None
    
def generate_expression(t, ctx, d=0):
    if random.choice(range(2)) == 0:
        v = get_var(t, ctx)
        if v:
            return v;

    if t == 'Boolean':
        v = get_var('Integer', ctx)
        if v:
            return "%s %% %d == 0" % (v, random.randint(1,10))
        return "%s > %s" % (generate_expression('Integer', ctx, d+1), generate_expression('Integer', ctx, d+1))
    else:
        if d < REC_TREE and random.choice(range(3)) == 0:
            p = random.choice(range(5))
            if p == 0:
                return "%s + %s" % (generate_expression(t, ctx, d+1),generate_expression(t, ctx, d+1))
            if p == 1:
                return "%s - %s" % (generate_expression(t, ctx, d+1),generate_expression(t, ctx, d+1))
            if p == 2:
                return "%s * %s" % (generate_expression(t, ctx, d+1),generate_expression(t, ctx, d+1))
            if p == 3:
                return "Math.min(%s,%s)" % (generate_expression(t, ctx, d+1),generate_expression(t, ctx, d+1))
            if p == 4:
                return "Math.max(%s,%s)" % (generate_expression(t, ctx, d+1),generate_expression(t, ctx, d+1))
    
    if t == 'Long':
        if random.choice(range(2)) == 0 and d < REC_TREE:
            return "(long) (%s)" % generate_expression('Integer', ctx, d+1)
        elif random.choice(range(2)) == 0 and d < REC_TREE:
            return "Math.round(%s)" % generate_expression('Double', ctx, d+1)
        else:
            return "%dL" % random.randint(0,100000000)
    
    if t == 'Integer':
        return "%s" % random.randint(1,1000)

    if t == 'Double':
        p = random.choice(range(3))
        if d < REC_TREE:
            if p == 0:
                return "%s * 0.1" % generate_expression('Integer', ctx, d+1)
            if p == 1:
                return "Math.cos(%s)" % generate_expression(t, ctx, d+1)
            if p == 2:
                return "Math.tan(%s)" % generate_expression(t, ctx, d+1)
            if p == 3:
                return "Math.sin(%s)" % generate_expression(t, ctx, d+1)
            if p == 4:
                return "Math.log(%s)" % generate_expression(t, ctx, d+1)
            if p == 5:
                return "Math.pow(%s, %s)" % (generate_expression(t, ctx, d+1),
                                                generate_expression(t, ctx, d+1))
            if p == 6:
                return "Math.sqrt(%s)" % generate_expression(t, ctx, d+1)                
        return "%f" % random.random()
        
    if t == 'Float':
        p = random.choice(range(2))
        if d < REC_TREE:
            if p == 0:
                return "%s * 0.1f" % generate_expression('Integer', ctx, d+1)
        return "%ff" % random.random()
    return "%d" % random.randint(0,100);

def generate_statement(ctx, d = 0, leftRec = True):
    p = random.choice(range(7))
    if d < REC_TREE:
        if p == 0 and leftRec:
            counter = len(ctx)
            nt = random.choice(['Integer', 'Double', 'Long', 'Float'])
            nctx = ctx + [('var%d' % counter, nt)]
            s = "%s var%d = 1;\n%s" % (lower_type(nt), counter, generate_statement(nctx, d+1))
            return s
        if p == 1:
            cond = generate_expression('Boolean', ctx, d+1)
            return """ if (%s) { %s } else { %s }
            """ % (cond, generate_statement(ctx, d+1, leftRec=False), generate_statement(ctx, d+1, leftRec=False))
        if p == 2:
            return """%s \n %s""" % (generate_statement(ctx, d+1, leftRec = False), generate_statement(ctx, d+1, leftRec=False))
            
        if p == 3:
            ci = len(ctx)
            st = generate_statement(ctx + [ ("i%d" % ci, 'Integer') ] , d+1, leftRec = False)
            return """ for(int i%d = 0; i%d < %d; i%d++) { %s }""" % (ci, ci, random.randint(0,1000000), ci, st)
            
    if p == 4 or p == 5:
        var, vt = random.choice(ctx)
        if var:
            return """%s = %s;""" % (var, generate_expression(vt,ctx, d+1))
    return ""

def generate_input(t):
    if t == 'Integer':
        lst = 'Int'
    else:
        lst = t
        
    conv = i
    if t in ['Float', 'Double']:
        conv = "i * 0.1"
        if t == 'Float':
            conv = "(float) (%s)" % conv
    if t == 'Long':
        conv = "(long) (%s)" % conv    
        
    return "%sList a = new %sList(); for (int i=0; i < SIZE; i++) a.add(%s); a" % (lst, lst, conv)

def generate_map(t):
    code = generate_statement([('ret', t), ('input', t) ])
    tc = lower_type(t)
    td = default_type(t)
    return """.map(new LambdaMapper<%s,%s>() {
        
        @Override
		public %s map(%s input) {
            %s ret = %s;
            %s
			return ret;
		}
    })""" % (t, t, t, t, tc, td, code)

def generate_reduce(t):
    code = generate_statement([('ret', t), ('input', t), ('other', t)])
    tc = lower_type(t)
    td = default_type(t)
    return """.reduce(new LambdaReducerWithSeed<%s>() {

			@Override
			public %s combine(%s input, %s other) {
                %s ret = %s;
                %s
				return ret;
			}

			@Override
			public %s getSeed() {
				return %s;
			}

		})""" % (t, t, t, t, tc, td, code, t, td)

def generate_out(root=False):
    r = random.choice([1,2,3])
    
    if r == 1:
        t = random.choice(types)
        return generate_input(t) + generate_map(t) + ".get(0);"
    elif r == 2:
        t = random.choice(types)
        return generate_input(t) + generate_reduce(t) + ";"
    else:
        t = random.choice(types)
        return generate_input(t) + generate_map(t) + generate_reduce(t) + ";"


def generate_program(i = 1):
    generated_code = generate_out(root = True)
    output =  source(i, generated_code)
    open("%s/Synthetic%d.java" % (package_folder, i), 'w').write(output)

if __name__ == '__main__':
    if len(sys.argv) <= 2:
        print "Not enough arguments"
        sys.exit(1)
    
    start = int(sys.argv[1])
    end = int(sys.argv[2])

    for i in range(start, end):
        random.seed(i)
        print "Generating %d..." % i
        generate_program(i)
        print "Compiling %d..." % i
        os.system("./utils/aegpuc %s/synthetic/Synthetic%d.java" % (target_folder, i))
        os.system("rm %s/synthetic/Synthetic%d.java" % (target_folder, i))
        
   