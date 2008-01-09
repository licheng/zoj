#include <iostream>
#include <fstream>
#include <vector>
#include <string>
#include <algorithm>
#include <sstream>
using namespace std;
ifstream fin("form.txt");
string className;
vector<string> lines;
vector<string> names;
vector<string> NAMES;
vector<string> type;

ofstream fout("form.java");
#define cout fout

void head() {
	getline(fin, className);
	
	cout << "/*" << endl;
    cout << " * Copyright (C) 2001 - 2005 ZJU Online Judge, All Rights Reserved." << endl;
    cout << " */" << endl;
    cout << "package cn.edu.zju.acm.onlinejudge.bean;" << endl;
    cout << "" << endl;
    cout << "" << endl;
    cout << "/**" << endl;
    cout << " * <p>" << endl;
    cout << " * " << className << " bean." << endl;
    cout << " * </p>" << endl;
    cout << " *" << endl;
    cout << " * @author ZOJDEV" << endl;
    cout << " * @version 2.0" << endl;
    cout << " */" << endl;	
    cout << "public class " << className << " {" << endl;
	cout << endl;
}

void tail() {

	cout << "}" << endl;
}

void middle() {
    cout << "    /**" << endl;
    cout << "     * Empty constructor." << endl;
    cout << "     */" << endl;
    cout << "    public " << className << "() {" << endl;
    cout << "        // Empty constructor" << endl;
    cout << "    }" << endl;
	cout << endl;
}


void genString(int i) {
		cout << "    /**" << endl;
		cout << "     * Sets the " << lines[i] << "."<< endl;
		cout << "     *"<< endl;
		cout << "     * @prama " << names[i] << " the " << lines[i] << " to set."<< endl;
		cout << "     */"<< endl;
		cout << "    public void set" << NAMES[i] << "(String " << names[i] << ") {" << endl;
		cout << "        this." << names[i] << " = " << names[i] << ";" << endl;
		cout << "    }" << endl;
		cout << endl;

		cout << "    /**" << endl;
		cout << "     * Gets the " << lines[i] << "."<< endl;
		cout << "     *"<< endl;
		cout << "     * @return the " << lines[i] << "." << endl;
		cout << "     */"<< endl;
		cout << "    public " << type[i] << " get" << NAMES[i] << "() {" << endl;
		cout << "        return " << names[i] << ";" << endl;
		cout << "    }" << endl;
		cout << endl;
}

void genBoolean(int i) {
		cout << "    /**" << endl;
		cout << "     * Sets the " << lines[i] << " flag."<< endl;
		cout << "     *"<< endl;
		cout << "     * @prama " << names[i] << " the " << lines[i] << " flag to set."<< endl;
		cout << "     */"<< endl;
		cout << "    public void set" << NAMES[i] << "(boolean " << names[i] << ") {" << endl;
		cout << "        this." << names[i] << " = " << names[i] << ";" << endl;
		cout << "    }" << endl;
		cout << endl;

		cout << "    /**" << endl;
		cout << "     * Gets the " << lines[i] << " flag."<< endl;
		cout << "     *"<< endl;
		cout << "     * @return the " << lines[i] << " flag." << endl;
		cout << "     */"<< endl;
		cout << "    public boolean is" << NAMES[i] << "() {" << endl;
		cout << "        return " << names[i] << ";" << endl;
		cout << "    }" << endl;
		cout << endl;
}

int main() {
	
	head();	

	for (;;) {
		string line;
		getline(fin, line);
		if (fin.fail() || line.size() == 0) break;
		int pos = line.find_last_of(" ");
		if (pos != string::npos) {
			string tmp = line.substr(pos + 1);
			if (tmp == "boolean") {
				type.push_back(line.substr(pos + 1));
				line = line.substr(0, pos);
			} else {
				type.push_back("String");
			}
	
		} else {
			type.push_back("String");
		}

		istringstream sin(line);
				
		lines.push_back(line);
		string NAME;
		for (;;) {
			string temp;
			sin >> temp;
			if (sin.fail()) break;
			if (temp[0] <='z' && temp[0] >= 'a') temp[0] -= 32;
			NAME += temp;			
		}
		string name = NAME;
		name[0] += 32;
		NAMES.push_back(NAME);
		names.push_back(name);
	}
	int i;

	for (i = 0; i < lines.size(); ++i) {
		cout << "    /**" << endl;
		cout << "     * The " << lines[i] << (type[i] == "boolean" ? " flag" : "") <<"."<< endl;
		cout << "     */"<< endl;
		cout << "    private " << type[i] << " " << names[i] << " = " << (type[i] == "boolean" ? "false" : "null") << ";" << endl;
		cout << endl;
	}

	middle();

	for (i = 0; i < lines.size(); ++i) {
		if (type[i] == "boolean") {
			genBoolean(i); 			
		} else {
			genString(i);
		} 

		
	}

	tail();
	


	return 0;
}
