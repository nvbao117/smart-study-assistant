import os
import re

def process_file(filepath):
    with open(filepath, 'r', encoding='utf-8') as f:
        content = f.read()
    
    if '@Inject constructor' in content:
        return

    # Pattern assumes 'class Name (' or 'class Name(' optionally across lines until ')'
    pattern = re.compile(r'(class\s+[A-Za-z0-9_]+)(?:\s|\n)*\(([\s\S]*?)\)(?:\s|\n)*(?::|\{)', re.MULTILINE)
    
    match = pattern.search(content)
    if match:
        # Re-construct with @Inject constructor
        def replacer(m):
            cls_name = m.group(1)
            args = m.group(2)
            # The rest of match needs careful handling, we just insert @Inject constructor before (
            return f"{cls_name} @Inject constructor({args})"
        
        # Actually safer to just insert
        pattern_safe = re.compile(r'(class\s+[A-Za-z0-9_]+)(\s*\()')
        
        def safe_rep(m):
            return f"{m.group(1)} @Inject constructor("

        new_content = pattern_safe.sub(safe_rep, content, count=1)
        
        if 'import javax.inject.Inject' not in new_content:
            new_content = re.sub(r'(package\s+[\w\.]+)', r'\1\n\nimport javax.inject.Inject', new_content, count=1)
            
        with open(filepath, 'w', encoding='utf-8') as f:
            f.write(new_content)

dirs = [
    'app/src/main/java/hcmute/edu/vn/smartstudyassistant/domain/usecase',
    'app/src/main/java/hcmute/edu/vn/smartstudyassistant/data/repository'
]

for d in dirs:
    for root, _, files in os.walk(d):
        for file in files:
            if file.endswith('.kt') and not file.endswith('Factory.kt'):
                print(f"Processing {file}")
                process_file(os.path.join(root, file))
