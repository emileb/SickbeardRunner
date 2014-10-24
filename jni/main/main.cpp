#define PY_SSIZE_T_CLEAN
#include "Python.h"

#include <stdio.h>
#include <stdlib.h>
#include <jni.h>
#include <unistd.h>
#include "android/log.h"
#include <deque>
#include <string>

//#define LOG(...) __android_log_print(ANDROID_LOG_INFO, "SBpython", __VA_ARGS__)
#define LOG(...)

using namespace std;


static int done_exit;
static int do_restart;

deque<string> *logQueue;

//Used to call back

jclass    pJNIActivityClass;
jmethodID pJNIMethodID;

static JavaVM *my_JavaVM;

extern "C" {

// get Java Environment
static JNIEnv *my_getJNIEnv ( )
{
	JNIEnv *pJNIEnv ;

	if ( my_JavaVM && ( my_JavaVM->GetEnv ( (void**) &pJNIEnv, JNI_VERSION_1_4 ) >= 0 ) )
	{
		return pJNIEnv ;
	}
	return NULL ;
}

JNIEXPORT jint JNICALL JNI_OnLoad(JavaVM *jvm, void *reserved) {

	LOG("JNI_onload");
	my_JavaVM = jvm;

	JNIEnv* pJNIEnv = my_getJNIEnv ( ) ;
	pJNIActivityClass = pJNIEnv->FindClass ( "com/emtronics/sickbeardrunner/PythonLib" ) ;
	pJNIMethodID      = pJNIEnv->GetStaticMethodID ( pJNIActivityClass, "callBack", "(I)V" ) ;

	logQueue = new deque<string>();

	return JNI_VERSION_1_6;
}

jint Java_com_emtronics_sickbeardrunner_PythonLib_getLogSize(JNIEnv* env,
		jobject obj) {
	return logQueue->size();
}

jstring Java_com_emtronics_sickbeardrunner_PythonLib_getLog(JNIEnv* env,
		jobject obj, jint jpos) {

	int pos = jpos;

	if (pos >= logQueue->size())
	{
		return env->NewStringUTF("LOG OUTOFRANGE");
	}
	else
	{
		string s = logQueue->at(pos);
		return env->NewStringUTF(s.c_str());
	}
}
jint Java_com_emtronics_sickbeardrunner_PythonLib_keyboardInterrupt(JNIEnv* env,
		jobject obj) {
	LOG("keyboardInterrupt");
	PyErr_SetInterrupt();
	PyErr_CheckSignals();
	return 0;
}


static PyObject *androidembed_log(PyObject *self, PyObject *args) {
	char *logstr = NULL;
	if (!PyArg_ParseTuple(args, "s", &logstr)) {
		return NULL;
	}
	LOG(logstr);

	string l = logstr;
	logQueue->push_front(l);

	while (logQueue->size() > 10000)
		logQueue->pop_back();

	JNIEnv* pJNIEnv;// = my_getJNIEnv ( ) ;

	//my_JavaVM->AttachCurrentThread(&pJNIEnv, NULL);

	bool isAttached = false;
	int status = my_JavaVM->GetEnv((void **) &pJNIEnv, JNI_VERSION_1_4);
	if(status < 0) {

		status = my_JavaVM->AttachCurrentThread(&pJNIEnv, NULL);

		if(status < 0) {
			LOG("callback_handler: failed to attach "
					"current thread");
			Py_RETURN_NONE;
		}
		isAttached = true;
	}

	// jclass    pJNIActivityClass = pJNIEnv->FindClass ( "com/emtronics/sickbeardrunner/PythonLib" ) ;
	//jmethodID pJNIMethodID      = pJNIEnv->GetStaticMethodID ( pJNIActivityClass, "callBack", "(I)V" ) ;
	//env_last->CallStaticVoidMethod(CallBackClass, javaCallBack,0);
	pJNIEnv->CallStaticVoidMethod ( pJNIActivityClass, pJNIMethodID, 0 ) ;
	if (isAttached) my_JavaVM->DetachCurrentThread();

	Py_RETURN_NONE;
}

static PyObject *androidembed_exit(PyObject *self, PyObject *args) {
	char *logstr = NULL;
	LOG("androidembed_exit called!!");
	done_exit = 1;
	//exit(0);
	Py_RETURN_NONE;
}

static PyObject *androidembed_do_restart(PyObject *self, PyObject *args) {
	char *logstr = NULL;
	LOG("androidembed_do_restart called!!");
	do_restart = 1;
	//exit(0);
	Py_RETURN_NONE;
}

static PyObject* androidembed_done_exit(PyObject *self, PyObject *args)
{
	return Py_BuildValue("i",done_exit);
}

static PyMethodDef AndroidEmbedMethods[] = {
		{"log",  androidembed_log, METH_VARARGS,	"Log on android platform"},
		{"exit", androidembed_exit, METH_NOARGS,	"System exit called"},
		{"do_restart", androidembed_do_restart, METH_NOARGS,	"Wants a restart"},
		{"done_exit", androidembed_done_exit,METH_NOARGS,	"System exit called"},
		{NULL, NULL, 0, NULL}
};

PyMODINIT_FUNC initandroidembed(void) {
	(void) Py_InitModule("androidembed", AndroidEmbedMethods);
}

int file_exists(const char * filename)
{
	FILE *file;
	if (file = fopen(filename, "r")) {
		fclose(file);
		return 1;
	}
	return 0;
}

int start(const char * progname,const  char * private_path,const char * app_path,int argc,char ** argv);

jint Java_com_emtronics_sickbeardrunner_PythonLib_main(JNIEnv* env,
		jobject obj, jstring jprivate_path, jstring japp_path, jstring japp_name,jobjectArray argsArray) {

	int argc=1;
	char * argv[32];
	argv[0] = (char*)"pyrun";

	jboolean isfilenameCopy;
	const char * private_path = (env)->GetStringUTFChars( jprivate_path,&isfilenameCopy);
	const char * app_path =     (env)->GetStringUTFChars( japp_path,&isfilenameCopy);
	const char * app_name =     (env)->GetStringUTFChars( japp_name,&isfilenameCopy);

	int argCount = (env)->GetArrayLength( argsArray);

	for (int i=0; i<argCount; i++) {
		jstring string = (jstring) (env)->GetObjectArrayElement( argsArray, i);
		argv[argc] = (char *)(env)->GetStringUTFChars( string, 0);
		argc++;
	}

	int ret = start(app_name,private_path,app_path,argc,argv);

	for (int i=0; i<argCount; i++) {
		jstring string = (jstring) (env)->GetObjectArrayElement( argsArray, i);
		(env)->ReleaseStringUTFChars(string, argv[i+1]);
	}

	(env)->ReleaseStringUTFChars(jprivate_path, private_path);
	(env)->ReleaseStringUTFChars(japp_path, app_path);
	(env)->ReleaseStringUTFChars(japp_name, app_name);

	return ret;
}

exception exite;
/*
void exitFunc()
{
	LOG("exitFunc called!!!!");
	throw exite;
	LOG("exitFuncafter exception");
}
 */
int start(const char * progname,const  char * private_path,const char * app_path,int argc,char ** argv) {


	int ret = 0;
	FILE *fd;

	LOG("Starting Python");

	done_exit = 0;
	do_restart = 0;

	setenv("PRIVATE_PATH", private_path, 1);
	setenv("APP_PATH", app_path, 1);


	setenv("PYTHONHOME",private_path,1);
	//setenv("PYTHONPATH",private_path,1);

	//IMPORTANT!!!
	setenv("PYTHONOPTIMIZE", "2",1);
	//setenv("PYTHONVERBOSE", "1", 1);

	//Py_AndroidSetExit(&exitFunc);

	Py_SetProgramName((char*)"pyrun");
	Py_Initialize();

	//char * args[1];
	//args[0] = (char*)"pyrun";
	//PySys_SetArgv(1, args);

	PySys_SetArgv(argc, argv);

	/* ensure threads will work.
	 */
	PyEval_InitThreads();

	/* our logging module for android
	 */
	initandroidembed();

	/* inject our bootstrap code to redirect python stdin/stdout
	 * replace sys.path with our path
	 */
	PyRun_SimpleString(
			"import sys, posix\n" \
			"private = posix.environ['PRIVATE_PATH']\n" \
			"argument = posix.environ['APP_PATH']\n" \
			"sys.path[:] = [ \n" \
			"    private + '/lib/python2.7/', \n" \
			"    private + '/lib/python2.7/lib-dynload/', \n" \
			"    private + '/lib/python2.7/site-packages/', \n" \
			"    argument ]\n" \
			"import androidembed\n" \
			"class LogFile(object):\n" \
			"    def __init__(self):\n" \
			"        self.buffer = ''\n" \
			"    def write(self, s):\n" \
			"        s = self.buffer + s\n" \
			"        lines = s.split(\"\\n\")\n" \
			"        for l in lines[:-1]:\n" \
			"            androidembed.log(l)\n" \
			"        self.buffer = lines[-1]\n" \
			"    def flush(self):\n" \
			"        return\n" \
			"sys.stdout = sys.stderr = LogFile()\n" \
			"def done_exit():\n" \
			"    if androidembed.done_exit() == 0:\n" \
			"        return False\n" \
			"    else:\n" \
			"        return True\n" \
			"def set_exit():\n" \
			"    print 'set_exit CALLED'\n" \
			"    androidembed.exit()\n" \
			"    return\n" \
			"class MyExit(object):\n" \
			"    def __init__(self):\n" \
			"        self.buffer = ''\n" \
			"    def exit(self):\n" \
			"        print ' '\n" \
			"        print 'EXIT CALLED'\n" \
			"        print ' '\n" \
			"        raise Exception('Exit Exception')\n" \
			"        #androidembed.exit()\n" \
			"    def exit1(self,n):\n" \
			"        print 'EXIT.1 CALLED'\n" \
			"        logging.exception('Something awful happened!')\n" \
			"        androidembed.exit()\n" \
			"    def flush(self):\n" \
			"        return\n" \
			"sys.exit = MyExit().exit\n" \
			"# os._exit = MyExit().exit1\n" \
			"import site;\n"\
			"#print site.getsitepackages()\n"\
			"#print 'Android path', sys.path\n" );

	/*
 			"class MyExit(object):\n" \
			"    def __init__(self):\n" \
			"        self.buffer = ''\n" \
			"    def exit(self):\n" \
			"        print 'EXIT CALLED'\n" \
			"        androidembed.exit()\n" \
			"    def exit1(self,n):\n" \
			"        print 'EXIT.1 CALLED'\n" \
			"        logging.exception('Something awful happened!')\n" \
			"        androidembed.exit()\n" \
			"    def done_exit(self):\n" \
			"        print 'done_exit CALLED'\n" \
			"        if androidembed.done_exit() == 0:\n" \
			"            print 'returning false'\n" \
			"            return False\n" \
			"        else:\n" \
			"            print 'returning true'\n" \
			"            return True\n" \
			"    def flush(self):\n" \
			"        return\n" \
			"# sys.exit = MyExit().exit\n" \
			"# os.exit = os._exit = MyExit().exit1\n" \
	 */
	const char *main_py = progname;
	/* run it !
	 */
	LOG("Run user program, change dir and execute %s",main_py);
	chdir(app_path);

	/* search the initial main.py
	 */

	if ( file_exists(main_py) == 0 ) {

		LOG("File %s not found",main_py);
		return -1;
	}


	fd = fopen(main_py, "r");
	if ( fd == NULL ) {
		LOG("Opening %s failed",main_py);
		return -1;
	}


	ret = PyRun_SimpleFile(fd, main_py);

	PyErr_Clear();
	/*
	if (PyErr_Occurred() != NULL) {
		ret = 1;
		PyErr_Print(); //This exits with the right code if SystemExit.
		if (Py_FlushLine())
			PyErr_Clear();
	}
	 */

	/* close everything
	 */
	Py_Finalize();
	fclose(fd);

	LOG("Python for android ended.");
	return do_restart;
}
}
