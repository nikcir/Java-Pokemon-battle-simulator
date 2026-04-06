import os  
import subprocess
from collections import defaultdict

def count_lines_of_code():
  """
  Counts total lines and breaks down by file type (.java, .fxml, .css, etc.)
  Excludes compiled artifacts and build outputs.
  """

  # Extensions to count (source code only)
  SOURCE_EXTENSIONS = {'.java', '.fxml', '.css', '.xml', '.json', '.properties', '.md'}

  result = subprocess.run(['git', 'ls-files'], capture_output=True, text=True)
  files = result.stdout.splitlines()

  total_lines = 0
  lines_by_type = defaultdict(int)

  for file in files:
    _, ext = os.path.splitext(file)
    
    # Only count specified source file types
    if ext not in SOURCE_EXTENSIONS:
      continue
    
    try:
      with open(file, 'r', errors='ignore') as f:
        line_count = sum(1 for _ in f)
        total_lines += line_count
        lines_by_type[ext] += line_count
    except Exception as e:
      print(f"Error reading {file}: {e}")

  # Print results
  print("=" * 50)
  print("Lines of Code by File Type (Source Only)")
  print("=" * 50)
  
  for ext in sorted(lines_by_type.keys()):
    print(f"{ext:15} {lines_by_type[ext]:10,} lines")
  
  print("=" * 50)
  print(f"{'TOTAL':15} {total_lines:10,} lines")
  print("=" * 50)

if __name__ == "__main__":
  count_lines_of_code()