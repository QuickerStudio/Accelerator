#!/usr/bin/env python3
"""
从 EcdictWords.kt 提取单词数据并转换为 JSON 格式
"""
import re
import json

def extract_words_from_kt(file_path):
    """从 Kotlin 文件中提取单词数据"""
    words = []

    with open(file_path, 'r', encoding='utf-8') as f:
        content = f.read()

    # 匹配 Word(...) 模式
    pattern = r'Word\((\d+),\s*"([^"]+)",\s*"([^"]*)",\s*"([^"]*)",\s*"([^"]*)",\s*(\d+),\s*"([^"]*)",\s*"([^"]*)"\)'

    matches = re.findall(pattern, content)

    for match in matches:
        word = {
            "id": int(match[0]),
            "word": match[1],
            "phonetic": match[2],
            "translation": match[3].replace('\\n', '\n'),  # 恢复换行符
            "example": match[4],
            "frequency": int(match[5]),
            "level": match[6],
            "pos": match[7]
        }
        words.append(word)

    return words

def main():
    input_file = r'C:\Users\Quick\AndroidStudioProjects\Accelerator\app\src\main\java\com\english\accelerator\data\EcdictWords.kt'
    output_file = r'C:\Users\Quick\AndroidStudioProjects\Accelerator\app\src\main\res\raw\ecdict_words.json'

    print(f"Extracting words from {input_file}...")
    words = extract_words_from_kt(input_file)

    print(f"Extracted {len(words)} words")

    # Save as JSON
    with open(output_file, 'w', encoding='utf-8') as f:
        json.dump(words, f, ensure_ascii=False, indent=2)

    print(f"Saved to {output_file}")
    print(f"File size: {len(json.dumps(words, ensure_ascii=False)) / 1024:.2f} KB")

if __name__ == '__main__':
    main()
