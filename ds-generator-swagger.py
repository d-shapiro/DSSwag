import subprocess, shutil
from os import listdir
from os.path import isfile, join

CODEGEN_V2 = 'swagger-codegen-cli-2.3.1.jar'
CODEGEN_V3 = 'swagger-codegen-cli-3.0.0.jar'

def getopts(argv):
    opts = {}  # Empty dictionary to store key-value pairs.
    while argv:  # While there are arguments left to parse...
        if argv[0][0] == '-':  # Found a "-name value" pair.
            opts[argv[0]] = argv[1]  # Add key and value to the dictionary.
        argv = argv[1:]  # Reduce the argument list by copying it starting from index 1.
    return opts

def main(args):
    if '-i' not in args:
        raise ValueError('Must specify input file using -i')
    inp = args['-i']
    out = ""
    if '-o' in args:
        out = args['-o']
        if not out.endswith('/'):
            out = out + '/'
    out = out + 'temp'

    codegen = CODEGEN_V2
    if '-v' in args:
        if '3' in args['-v']:
            codegen = CODEGEN_V3

    subprocess.call(['java', '-jar', codegen, 'generate', '-i', inp, '-o', out, '-l', 'java'])

    shutil.copytree(out + "/docs", out[:-4] + "docs")
    shutil.copytree(out + "/src", out[:-4] + "src")

    shutil.copytree("dslink/gradle", out[:-4] + "gradle")
    shutil.copytree("dslink/dsa", out[:-4] + "dsa")
    shutil.copytree("dslink/src/main/java/org", out[:-4] + "src/main/java/org")
    shutil.copyfile("dslink/build.gradle", out[:-4] + "build.gradle")
    shutil.copyfile("dslink/dslink.json", out[:-4] + "dslink.json")
    shutil.copyfile("dslink/gradlew", out[:-4] + "gradlew")
    shutil.copyfile("dslink/gradlew.bat", out[:-4] + "gradlew.bat")

    if '-n' in args:
        name = args['-n']
        f = open(out[:-4] + "dslink.json")
        dslinkjson = f.read()
        f.close()
        dslinkjson = dslinkjson.replace("dslink-java-v2-swaggertemplate", name)
        f = open(out[:-4] + "dslink.json", 'w')
        f.write(dslinkjson)
        f.close()

    utils = ""
    f = open(out[:-4] + "src/main/java/org/iot/dsa/dslink/swagger/Utils.java")
    imports, adds = getApiClassesLines(out)
    for line in f:
        if "// API IMPORTS GO HERE" in line:
            utils += imports
        elif "// API CLASSES GO HERE" in line:
            utils += adds
        else:
            utils += line

    f.close()
    f = open(out[:-4] + "src/main/java/org/iot/dsa/dslink/swagger/Utils.java", 'w')
    f.write(utils)
    f.close()

def getApiClassesLines(out):
    apidir = out[:-4] + "src/main/java/io/swagger/client/api"
    apifiles = [f for f in listdir(apidir) if isfile(join(apidir, f)) and f.endswith(".java")]
    adds = ""
    imports = ""
    for api in apifiles:
        adds += "            apiClasses.add(" + api[:-5] + ".class);\n"
        imports += "import io.swagger.client.api." + api[:-5] + ";"
    return imports, adds


if __name__ == '__main__':
    from sys import argv
    myargs = getopts(argv)
    main(myargs)
