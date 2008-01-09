<%@ page contentType="text/html; charset=utf-8" language="java" %>
<div id="content_title">Frequently Asked Questions</div>
<div id="content_body">
<hr>
<font color=green>Q</font>:What is the compiler the judge is using and what are the compiler options?<br>
<font color=red>A</font>:The online judge system is running on Linux. We are using <a href="http://gcc.gnu.org">GNU GCC/G++</a> for C/C++ compile and <a href="http://www.gnu-pascal.de">GPC</a>/<a href="http://www.freepascal.org">Free Pascal</a> for pascal compile. The compile options are:<br>
C: <font color=blue>gcc foo.c -o foo -ansi -fno-asm -O2 -Wall -lm --static -DONLINE_JUDGE</font><br>
C++: <font color=blue>g++ foo.c -o foo -ansi -fno-asm -O2 -Wall -lm --static -DONLINE_JUDGE</font><br>
GNU Pascal(GPC): <font color=blue>gpc foo.pas -o foo -fno-asm -Wall -lm --static -DONLINE_JUDGE</font><br>
Free Pascal(FPC): <font color=blue>fpc -Co -Cr -Ct -Ci -dONLINE_JUDGE</font><br>
Our compiler software version:<br>
<font color=blue>gcc/g++ 3.2.2 20030222 (Red Hat Linux 3.2.2-5)</font><br>
<font color=blue>glibc 2.3.5</font><br>
<font color=blue>GNU Pascal version 20041218, based on gcc-3.3.3</font><br>
<font color=blue>Free Pascal Compiler version 2.0.1 [2005/09/23] for i386</font><br>
<hr>
<font color=green>Q</font>:What does SIGSEGV in Runtime Error stand for?<br>
<font color=red>A</font>:The following messages will not be shown to you in contest. Here we just provide some tips: <br>
<B>SIGSEGV</B> --- Segment Fault. The possible cases of your encountering this error are:<br>
<ul>
<li>1.buffer overflow --- usually caused by a pointer reference out of range.<br> 
<li>2.stack overflow --- please keep in mind that the default stack size is 8192K.<br>
<li>3.illegal file access --- file operations are forbidden on our judge system.<br>
</ul>
<B>SIGFPE</B> &nbsp;--- Divided by 0<br>
<B>SIGBUS</B> &nbsp;--- Hardware Error. //please contact us<br>
<B>SIGABRT</B> --- Programme aborted before it should be finished.<br>
<B>man 7 signal</B> under Linux for more information<br> 
<hr>
<font color=green>Q</font>:Free Pascal Runtime Error Numbers<br>
<font color=red>A</font>:Refer to here <a href="http://www.freepascal.org/docs-html/user/node16.html">http://www.freepascal.org/docs-html/user/node16.html</a> for detailed runtime error informations.<br>
We list some frequently used error numbers here:<br>
<li><B>200</B> Division by zero
<li><B>201</B> Range check error
<li><B>202</B> Stack overflow error
<li><B>203</B> Heap overflow error
<li><B>204</B> Invalid pointer operation
<li><B>205</B> floating point overflow
<li><B>206</B> floating point underflow
<li><B>207</B> invalid floating point operation
<li><B>216</B> General Protection fault
<hr>
<font color=green>Q</font>:Where is the input and the output?<br>
<font color=red>A</font>:Your program shall read input from stdin('Standard Input') and write output to stdout('Standard Output').For example,you can use 'scanf' in C or 'cin' in C++ to read from stdin,and use 'printf' in C or 'cout' in C++ to write to stdout.<br>
User programs are not allowed to open and read from/write to files, you will get a "<font color=green>Runtime Error</font>" if you try to do so.<br>
<a name="sample">Here is a sample solution for problem 1001 using C++:</a><br>
<pre>
<font color="20B000" size=5>#include &lt;iostream&gt;
using namespace std;

int main()
{
    int a,b;
    while(cin >> a >> b)
        cout << a+b << endl;
}
</font>
</pre>
Here is a sample solution for problem 1001 using C:<br>
<pre>
<font color="20B000" size=5>#include &lt;stdio.h&gt;

int main()
{
    int a,b;
    while(scanf("%d %d",&amp;a, &amp;b) != EOF)
        printf("%d\n",a+b);
}
</font>
</pre>
Here is a sample solution for problem 1001 using PASCAL(both GPC&FPC):<br>
<pre>
<font color="20B000" size=5>
program p1001(Input,Output); 
var 
  a,b:Integer; 
begin 
   while not eof(Input) do 
     begin 
       Readln(a,b); 
       Writeln(a+b); 
     end; 
end.
</font>
</pre>
<hr>
<font color=green>Q</font>:Why did I get a Compile Error? It's well done!<br>
<font color=red>A</font>:There are some differences between GNU and MS-VC++, such as:<br>
<ul>
<li><font color=blue>main</font> must be declared as <font color=blue>int</font>, <font color=blue>void main</font> will end up with a Compile Error.<br> 
<li><font color=green>i</font> is out of definition after block "<font color=blue>for</font>(<font color=blue>int</font> <font color=green>i</font>=0...){...}"<br>
<li><font color=green>itoa</font> is not an ANSI function.<br>
<li><font color=green>__int64</font> of VC is not ANSI, but you can use <font color=blue>long long</font> for 64-bit integer.<br>
</ul>
<hr>
<font color=green>Q</font>:What is the meaning of the judge's reply XXXXX?<br>
<font color=red>A</font>:Here is a list of the judge's replies and their meaning:<br>
<p>
<font color=blue>Queuing</font> : The judge is so busy that it can't judge your submit at the moment, usualy you just need to wait a minute and your submit will be judged.<br>
<br>
<font color=blue>Accepted</font> : OK! Your program is correct!.<br>
<br>
<font color=blue>Presentation Error</font> : Your output format is not exactly the same as the judge's output, although your answer to the problem is correct. Check your output for spaces, blank lines,etc against the problem output specification.<br>
<br>
<font color=blue>Wrong Answer</font> : Correct solution not reached for the inputs. The inputs and outputs that we use to test the programs are not public (it is recomendable to get accustomed to a true contest dynamic ;-).<br>
<br>
<font color=blue>Runtime Error</font> : Your program failed during the execution (segmentation fault, floating point exception...). The exact cause is reported to the user.<br>
<br>
<font color=blue>Time Limit Exceeded</font> : Your program tried to run during too much time.<br>
<br>
<font color=blue>Memory Limit Exceeded</font> : Your program tried to use more memory than the judge default settings.  <br>
<br>
<font color=blue>Output Limit Exceeded</font>: Your program tried to write too much information. This usually occurs if it goes into a infinite loop. Currently the output limit is 1M bytes.<br>
<br>
<font color=blue>Compile Error</font> : The compiler (gcc/g++/gpc) could not compile your ANSI program. Of course, warning messages are not error messages. Click the link at the judge reply to see the actual error message.<br>
<br>
<font color=blue>Out Of Contest Time</font>: this message can only appear during a contest, if a program is submitted out of contest time. <br>
<br>
<font color=blue>No such problem</font>: Either you have submitted a wrong problem id or the problem is unavailable.<br>
</p>
<hr>
</div>