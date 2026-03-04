#!/bin/bash

COPYRIGHT='/**
 * Copyright © 2026 Quicker Studio
 * Licensed under CC BY-NC-ND 4.0
 * 版权归原作者 Quicker Studio 所有，禁止商业使用和修改
 */'

while IFS= read -r file; do
    # 跳过已有版权声明的文件
    if grep -q "Copyright © 2026 Quicker Studio" "$file"; then
        continue
    fi
    
    # 读取文件内容
    content=$(cat "$file")
    
    # 在文件开头添加版权声明
    echo "$COPYRIGHT" > "$file.tmp"
    echo "$content" >> "$file.tmp"
    mv "$file.tmp" "$file"
    
    echo "已处理: $file"
done < files_to_update.txt

echo "完成！"
