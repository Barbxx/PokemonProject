
import os
import re

def check_javadoc(file_path):
    with open(file_path, 'r', encoding='utf-8') as f:
        content = f.read()

    # Simple check: Does the class definition have a Javadoc block above it?
    # Look for "public class" or "public interface" or "public enum"
    # Then check if there is a '/**' block closely preceding it.
    
    lines = content.split('\n')
    missing_docs = []
    
    class_pattern = re.compile(r'^\s*public\s+(class|interface|enum)\s+(\w+)')
    method_pattern = re.compile(r'^\s*(public|protected|private)\s+[\w<>]+\s+(\w+)\(')
    
    in_javadoc = False
    last_javadoc_end = -1
    
    for i, line in enumerate(lines):
        if '/**' in line:
            in_javadoc = True
        if '*/' in line:
            in_javadoc = False
            last_javadoc_end = i
            
        match_class = class_pattern.match(line)
        if match_class:
            # Check if we had a javadoc ending recently (e.g. within last 5 lines, ignoring annotations)
            # This is a heuristic.
            has_doc = False
            # Check backwards from current line
            for j in range(i-1, i-10, -1):
                if j < 0: break
                l = lines[j].strip()
                if l == '*/':
                    has_doc = True
                    break
                if l.startswith('@'): continue # Annotations
                if l == '': continue
                if not l.startswith('@'): break # Non-empty, non-annotation line means no doc attached directly
            
            if not has_doc:
                missing_docs.append(f"Class: {match_class.group(2)}")

    if missing_docs:
        print(f"File: {file_path}")
        for m in missing_docs:
            print(f"  - Missing Javadoc for {m}")

def main():
    root_dir = r"c:\Users\User\PokemonProject\core\src\main\java"
    for subdir, dirs, files in os.walk(root_dir):
        for file in files:
            if file.endswith(".java"):
                check_javadoc(os.path.join(subdir, file))

if __name__ == "__main__":
    main()
