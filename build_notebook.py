import json
import os

def create_colab_notebook():
    cells = []

    def add_md(text):
        cells.append({
            "cell_type": "markdown",
            "metadata": {},
            "source": [line + "\n" for line in text.strip().split("\n")]
        })

    def add_code(code):
        cells.append({
            "cell_type": "code",
            "execution_count": None,
            "metadata": {},
            "outputs": [],
            "source": [line + "\n" for line in code.strip().split("\n")]
        })

    # Header Markdown
    add_md("""# Python Data Analysis and Visualization
### Foundations of Data Science (FDS) — Unit IV & Unit V
This Google Colab notebook contains all the Python code examples and implementations from **Unit IV (Handling Data with Pandas)** and **Unit V (Data Visualization with Matplotlib & Pandas)**.

---
## 📑 Table of Contents
### **UNIT IV: Handling Data (Pandas & Data Processing)**
1. [Pandas Series - Basic Creation](#u4_1)
2. [Pandas Series - Custom Labels (Index)](#u4_2)
3. [Pandas Series - From Dictionary](#u4_3)
4. [Pandas Series - From Dictionary with Selected Index](#u4_4)
5. [Creating DataFrame from a List](#u4_5)
6. [Creating DataFrames from Dictionary & List](#u4_6)
7. [DataFrame Slicing (Rows using iloc)](#u4_7)
8. [DataFrame Slicing (Columns using iloc)](#u4_8)
9. [Custom Indexing on DataFrame](#u4_9)
10. [Accessing Specific Columns](#u4_10)
11. [Accessing Multiple Columns by Index Range](#u4_11)
12. [Accessing Specific Rows & Columns using loc](#u4_12)
13. [Column Range Selection using loc](#u4_13)
14. [Accessing Single Row with All Columns using loc](#u4_14)
15. [Selection using iloc (Positional Indexing)](#u4_15)
16. [Adding a New Column to a DataFrame](#u4_16)
17. [Logical Operations on DataFrames](#u4_17)

### **UNIT V: Data Visualization (Matplotlib & Pandas Plotting)**
1. [Basic Line Plot (Pyplot)](#u5_1)
2. [Line Plot with Specific Points](#u5_2)
3. [Plotting with Markers](#u5_3)
4. [Plotting with Custom Line Style](#u5_4)
5. [Adding Axis Labels](#u5_5)
6. [Adding Title and Axis Labels](#u5_6)
7. [Adding Grid Lines](#u5_7)
8. [Creating Side-by-Side Subplots (1 Row, 2 Columns)](#u5_8)
9. [Creating Stacked Subplots (2 Rows, 1 Column)](#u5_9)
10. [Pandas Line Plot](#u5_10)
11. [Pandas Bar Plots](#u5_11)
12. [Pandas Box Plots](#u5_12)
13. [Pandas Area Plots](#u5_13)
14. [Pandas Scatter Plots](#u5_14)
15. [Pie Chart](#u5_15)
16. [Histograms](#u5_16)
""")

    # Import libraries setup
    add_md("### Initial Setup & Package Imports")
    add_code("""import numpy as np
import pandas as pd
import matplotlib.pyplot as plt

# Optional formatting configuration
pd.set_option('display.max_columns', None)
print("Libraries imported successfully!")
print(f"Pandas version: {pd.__version__}")
print(f"NumPy version: {np.__version__}")
""")

    # ------------------ UNIT IV ------------------
    add_md("""---
# 📦 UNIT IV: HANDLING DATA (Pandas & Data Processing)
Pandas is a fast, powerful, flexible, and easy-to-use open source data analysis and manipulation tool built on top of the Python programming language.
""")

    # 1. Pandas Series - Basic Creation
    add_md("""<a id="u4_1"></a>
### 1. Pandas Series - Basic Creation
A Pandas Series is like a column in a table. It is a one-dimensional array holding data of any type.
**Expected Output:**
```
0    1
1    7
2    2
dtype: int64
```
""")
    add_code("""import pandas as pd

a = [1, 7, 2]
myvar = pd.Series(a)
print(myvar)
""")

    # 2. Pandas Series - Custom Labels (Index)
    add_md("""<a id="u4_2"></a>
### 2. Pandas Series - Custom Labels (Index)
With the `index` argument, you can name your own labels.
**Expected Output:**
```
x    1
y    7
z    2
dtype: int64
```
""")
    add_code("""import pandas as pd

a = [1, 7, 2]
myvar = pd.Series(a, index=["x", "y", "z"])
print(myvar)
""")

    # 3. Pandas Series - From Dictionary
    add_md("""<a id="u4_3"></a>
### 3. Pandas Series - From Dictionary
You can also use a key/value object, like a dictionary, when creating a Series.
**Expected Output:**
```
day1    420
day2    380
day3    390
dtype: int64
```
""")
    add_code("""import pandas as pd

calories = {"day1": 420, "day2": 380, "day3": 390}
myvar = pd.Series(calories)
print(myvar)
""")

    # 4. Pandas Series - From Dictionary with Selected Index
    add_md("""<a id="u4_4"></a>
### 4. Pandas Series - From Dictionary with Selected Index
To select only some of the items in the dictionary, use the `index` argument and specify only the items you want to include in the Series.
**Expected Output:**
```
day1    420
day2    380
dtype: int64
```
""")
    add_code("""import pandas as pd

calories = {"day1": 420, "day2": 380, "day3": 390}
myvar = pd.Series(calories, index=["day1", "day2"])
print(myvar)
""")

    # 5. Creating DataFrame from a List
    add_md("""<a id="u4_5"></a>
### 5. Creating DataFrame from a List
A Pandas DataFrame can be created using a single list or a list of lists.
**Expected Output:**
```
             0
0  Foundations
1           of
2  Datascience
3           is
4            a
5         very
6  interesting
7      subject
```
""")
    add_code("""import pandas as pd

lst = [
    'Foundations',
    'of',
    'Datascience',
    'is',
    'a',
    'very',
    'interesting',
    'subject',
]
df = pd.DataFrame(lst)
print(df)
""")

    # 6. Creating DataFrames from Dictionary & List
    add_md("""<a id="u4_6"></a>
### 6. Creating DataFrames from Dictionary & List
Demonstrating creation of DataFrames from a dictionary of lists and a standard list.
**Expected Output:**
```
   calories  duration
0       420        50
1       380        40
2       390        45

             0
0  Foundations
1           of
2  Datascience
3           is
4            a
5         very
6  interesting
7      subject
```
""")
    add_code("""import pandas as pd

lst = [
    'Foundations',
    'of',
    'Datascience',
    'is',
    'a',
    'very',
    'interesting',
    'subject',
]
data = {"calories": [420, 380, 390], "duration": [50, 40, 45]}

df = pd.DataFrame(data)
print(df)
print()

df1 = pd.DataFrame(lst)
print(df1)
""")

    # 7. DataFrame Slicing (Rows using iloc)
    add_md("""<a id="u4_7"></a>
### 7. DataFrame Slicing (Rows using iloc)
Using `iloc` with row slice `[0:4]` to extract the first 4 rows (indices 0 to 3).
**Expected Output:**
```
            Name  Age  Weight   Salary
0      M.S.Dhoni   36      75  5428000
1  A.B.D Villers   38      74  3428000
2        V.Kholi   31      70  8428000
3        S.Smith   34      80  4428000
```
""")
    add_code("""import pandas as pd

player_list = [
    ['M.S.Dhoni', 36, 75, 5428000],
    ['A.B.D Villers', 38, 74, 3428000],
    ['V.Kholi', 31, 70, 8428000],
    ['S.Smith', 34, 80, 4428000],
    ['C.Gayle', 40, 100, 4528000],
    ['J.Root', 33, 72, 7028000],
    ['K.Peterson', 42, 85, 2528000],
]

df = pd.DataFrame(player_list, columns=['Name', 'Age', 'Weight', 'Salary'])

# Slicing rows 0 to 3
df1 = df.iloc[0:4]
print(df1)
""")

    # 8. DataFrame Slicing (Columns using iloc)
    add_md("""<a id="u4_8"></a>
### 8. DataFrame Slicing (Columns using iloc)
Using `iloc[:, 0:2]` to select all rows and the first two columns (`Name` and `Age`).
**Expected Output:**
```
            Name  Age
0      M.S.Dhoni   36
1  A.B.D Villers   38
2        V.Kholi   31
3        S.Smith   34
4        C.Gayle   40
5         J.Root   33
6     K.Peterson   42
```
""")
    add_code("""# Slicing columns (all rows, first 2 columns: 0 and 1)
df1 = df.iloc[:, 0:2]
print(df1)
""")

    # 9. Custom Indexing on DataFrame
    add_md("""<a id="u4_9"></a>
### 9. Custom Indexing on DataFrame
Assigning custom alphabetic row indices `['A', 'B', 'C', 'D', 'E', 'F', 'G']` to a DataFrame.
**Expected Output:**
```
            Name  Age  Weight   Salary
A      M.S.Dhoni   36      75  5428000
B  A.B.D Villers   38      74  3428000
C        V.Kholi   31      70  8428000
D        S.Smith   34      80  4428000
E        C.Gayle   40     100  4528000
F         J.Root   33      72  7028000
G     K.Peterson   42      85  2528000
```
""")
    add_code("""import pandas as pd

player_list = [
    ['M.S.Dhoni', 36, 75, 5428000],
    ['A.B.D Villers', 38, 74, 3428000],
    ['V.Kholi', 31, 70, 8428000],
    ['S.Smith', 34, 80, 4428000],
    ['C.Gayle', 40, 100, 4528000],
    ['J.Root', 33, 72, 7028000],
    ['K.Peterson', 42, 85, 2528000],
]

df = pd.DataFrame(
    player_list,
    columns=['Name', 'Age', 'Weight', 'Salary'],
    index=['A', 'B', 'C', 'D', 'E', 'F', 'G'],
)
print(df)
""")

    # 10. Accessing Specific Columns
    add_md("""<a id="u4_10"></a>
### 10. Accessing Specific Columns
Selecting a subset of columns (`Name` and `Qualification`) by passing a list of column names.
**Expected Output:**
```
     Name Qualification
0     Jai           Msc
1  Princi            MA
2  Gaurav           MCA
3    Anuj           Phd
```
""")
    add_code("""import pandas as pd

data = {
    'Name': ['Jai', 'Princi', 'Gaurav', 'Anuj'],
    'Age': [27, 24, 22, 32],
    'Address': ['Delhi', 'Kanpur', 'Allahabad', 'Kannauj'],
    'Qualification': ['Msc', 'MA', 'MCA', 'Phd'],
}

df = pd.DataFrame(data)
print(df[['Name', 'Qualification']])
""")

    # 11. Accessing Multiple Columns by Index Range
    add_md("""<a id="u4_11"></a>
### 11. Accessing Multiple Columns by Index Range
Selecting columns using index slice notation via `df.columns[1:4]`.
**Expected Output:**
```
   Age    Address Qualification
0   27      Delhi           Msc
1   24     Kanpur            MA
2   22  Allahabad           MCA
3   32    Kannauj           Phd
```
""")
    add_code("""# Select all rows and 2nd to 4th column (index 1 up to 3)
print(df[df.columns[1:4]])
""")

    # 12. Accessing Specific Rows & Columns using loc
    add_md("""<a id="u4_12"></a>
### 12. Accessing Specific Rows & Columns using loc
`loc` is label-based indexing. Note that row label slice `1:3` includes both row 1, 2, and 3.
**Expected Output:**
```
     Name Qualification
1  Princi            MA
2  Gaurav           MCA
3    Anuj           Phd
```
""")
    add_code("""# Select rows 1 to 3 and specified columns
print(df.loc[1:3, ['Name', 'Qualification']])
""")

    # 13. Column Range Selection using loc
    add_md("""<a id="u4_13"></a>
### 13. Column Range Selection using loc
Selecting a range of column labels from `'Name'` to `'Address'` for rows 0 to 1.
**Expected Output:**
```
     Name  Age Address
0     Jai   27   Delhi
1  Princi   24  Kanpur
```
""")
    add_code("""# Select rows 0 to 1 and columns from "Name" to "Address"
print(df.loc[0:1, 'Name':'Address'])
""")

    # 14. Accessing Single Row with All Columns using loc
    add_md("""<a id="u4_14"></a>
### 14. Accessing Single Row with All Columns using loc
Using `loc[0, :]` returns a Series representing the entire first row.
**Expected Output:**
```
Name               Jai
Age                 27
Address          Delhi
Qualification      Msc
Name: 0, dtype: object
```
""")
    add_code("""# Row index 0, all columns
print(df.loc[0, :])
""")

    # 15. Selection using iloc (Positional Indexing)
    add_md("""<a id="u4_15"></a>
### 15. Selection using iloc (Positional Indexing)
`iloc` is strictly integer position-based indexing (0 to length-1).

**Example 1:** All rows, first two columns (`[:, 0:2]`)
**Expected Output:**
```
     Name  Age
0     Jai   27
1  Princi   24
2  Gaurav   22
3    Anuj   32
```

**Example 2:** Rows 0:2 and columns 1:3 (`[0:2, 1:3]`)
**Expected Output:**
```
   Age Address
0   27   Delhi
1   24  Kanpur
```
""")
    add_code("""# Select all rows, first two columns
print("All rows, first two columns:")
print(df.iloc[:, 0:2])
print()

# Select rows 0:2 (rows 0 and 1) and columns 1:3 (columns 1 and 2)
print("Rows 0:2, Columns 1:3:")
print(df.iloc[0:2, 1:3])
""")

    # 16. Adding a New Column to a DataFrame
    add_md("""<a id="u4_16"></a>
### 16. Adding a New Column to a DataFrame
You can add a new column to an existing DataFrame by declaring a new list as a column attribute.
**Expected Output:**
```
     Name  Height Qualification    Address
0     Jai     5.1           Msc      Delhi
1  Princi     6.2            MA  Bangalore
2  Gaurav     5.1           Msc    Chennai
3    Anuj     5.2           Msc      Patna
```
""")
    add_code("""import pandas as pd

data = {
    'Name': ['Jai', 'Princi', 'Gaurav', 'Anuj'],
    'Height': [5.1, 6.2, 5.1, 5.2],
    'Qualification': ['Msc', 'MA', 'Msc', 'Msc'],
}

df = pd.DataFrame(data)

# New column list
address = ['Delhi', 'Bangalore', 'Chennai', 'Patna']
df['Address'] = address

print(df)
""")

    # 17. Logical Operations on DataFrames
    add_md("""<a id="u4_17"></a>
### 17. Logical Operations on DataFrames
Applying logical conditions across columns using `np.logical_and()`.
**Expected Output:**
```
        State  Score1  Score2  Pass_Status
0  Arizona AZ       4       5        False
1  Georgia GG      47      67         True
2  Newyork NY      55      54         True
3  Indiana IN      74      56         True
4  Florida FL      31      12        False
```
""")
    add_code("""import numpy as np
import pandas as pd

df1 = {
    'State': [
        'Arizona AZ',
        'Georgia GG',
        'Newyork NY',
        'Indiana IN',
        'Florida FL',
    ],
    'Score1': [4, 47, 55, 74, 31],
    'Score2': [5, 67, 54, 56, 12],
}

df1 = pd.DataFrame(df1, columns=['State', 'Score1', 'Score2'])

# Apply logical AND condition: Pass only if Score1 > 40 AND Score2 > 40
df1['Pass_Status'] = np.logical_and(df1['Score1'] > 40, df1['Score2'] > 40)

print(df1)
""")

    # ------------------ UNIT V ------------------
    add_md("""---
# 📊 UNIT V: DATA VISUALIZATION (Matplotlib & Pandas Plotting)
Data visualization is the graphical representation of information and data. By using visual elements like charts, graphs, and maps, data visualization tools provide an accessible way to see and understand trends, outliers, and patterns in data.
""")

    # 1. Basic Line Plot (Pyplot)
    add_md("""<a id="u5_1"></a>
### 1. Basic Line Plot (Pyplot)
Draw a line in a diagram from position (0,0) to position (6,250).
""")
    add_code("""import matplotlib.pyplot as plt
import numpy as np

xpoints = np.array([0, 6])
ypoints = np.array([0, 250])

plt.plot(xpoints, ypoints)
plt.show()
""")

    # 2. Line Plot with Specific Points
    add_md("""<a id="u5_2"></a>
### 2. Line Plot with Specific Points
Draw a line in a diagram from position (1, 3) to (8, 10).
""")
    add_code("""import matplotlib.pyplot as plt
import numpy as np

xpoints = np.array([1, 8])
ypoints = np.array([3, 10])

plt.plot(xpoints, ypoints)
plt.show()
""")

    # 3. Plotting with Markers
    add_md("""<a id="u5_3"></a>
### 3. Plotting with Markers
You can use the keyword argument `marker` to emphasize each point with a specified marker (e.g., `'o'`).
""")
    add_code("""import matplotlib.pyplot as plt
import numpy as np

ypoints = np.array([3, 8, 1, 10])

plt.plot(ypoints, marker='o')
plt.show()
""")

    # 4. Plotting with Custom Line Style
    add_md("""<a id="u5_4"></a>
### 4. Plotting with Custom Line Style
Use the keyword argument `linestyle` (or `ls`) to change the style of the plotted line (e.g., `'dotted'`).
""")
    add_code("""import matplotlib.pyplot as plt
import numpy as np

ypoints = np.array([3, 8, 1, 10])

plt.plot(ypoints, linestyle='dotted')
plt.show()
""")

    # 5. Adding Axis Labels
    add_md("""<a id="u5_5"></a>
### 5. Adding Axis Labels
With Pyplot, you can use the `xlabel()` and `ylabel()` functions to set labels for the x- and y-axis.
""")
    add_code("""import matplotlib.pyplot as plt
import numpy as np

x = np.array([80, 85, 90, 95, 100, 105, 110, 115, 120, 125])
y = np.array([240, 250, 260, 270, 280, 290, 300, 310, 320, 330])

plt.plot(x, y)
plt.xlabel("Average Pulse")
plt.ylabel("Calorie Burnage")
plt.show()
""")

    # 6. Adding Title and Axis Labels
    add_md("""<a id="u5_6"></a>
### 6. Adding Title and Axis Labels
With Pyplot, you can use the `title()` function to set a title for the plot.
""")
    add_code("""import matplotlib.pyplot as plt
import numpy as np

x = np.array([80, 85, 90, 95, 100, 105, 110, 115, 120, 125])
y = np.array([240, 250, 260, 270, 280, 290, 300, 310, 320, 330])

plt.plot(x, y)
plt.title("Sports Watch Data")
plt.xlabel("Average Pulse")
plt.ylabel("Calorie Burnage")
plt.show()
""")

    # 7. Adding Grid Lines
    add_md("""<a id="u5_7"></a>
### 7. Adding Grid Lines
With Pyplot, you can use the `grid()` function to add grid lines to the plot.
""")
    add_code("""import matplotlib.pyplot as plt
import numpy as np

x = np.array([80, 85, 90, 95, 100, 105, 110, 115, 120, 125])
y = np.array([240, 250, 260, 270, 280, 290, 300, 310, 320, 330])

plt.title("Sports Watch Data")
plt.xlabel("Average Pulse")
plt.ylabel("Calorie Burnage")
plt.plot(x, y)
plt.grid()
plt.show()
""")

    # 8. Creating Side-by-Side Subplots (1 Row, 2 Columns)
    add_md("""<a id="u5_8"></a>
### 8. Creating Side-by-Side Subplots (1 Row, 2 Columns)
`subplot(1, 2, 1)` and `subplot(1, 2, 2)` display multiple plots horizontally in a single figure.
""")
    add_code("""import matplotlib.pyplot as plt
import numpy as np

# Plot 1:
x = np.array([0, 1, 2, 3])
y = np.array([3, 8, 1, 10])
plt.subplot(1, 2, 1)
plt.plot(x, y)
plt.title("Plot 1")

# Plot 2:
x = np.array([0, 1, 2, 3])
y = np.array([10, 20, 30, 40])
plt.subplot(1, 2, 2)
plt.plot(x, y)
plt.title("Plot 2")

plt.tight_layout()
plt.show()
""")

    # 9. Creating Stacked Subplots (2 Rows, 1 Column)
    add_md("""<a id="u5_9"></a>
### 9. Creating Stacked Subplots (2 Rows, 1 Column)
`subplot(2, 1, 1)` and `subplot(2, 1, 2)` display multiple plots vertically stacked.
""")
    add_code("""import matplotlib.pyplot as plt
import numpy as np

# Plot 1:
x = np.array([0, 1, 2, 3])
y = np.array([3, 8, 1, 10])
plt.subplot(2, 1, 1)
plt.plot(x, y)
plt.title("Subplot 1 (Top)")

# Plot 2:
x = np.array([0, 1, 2, 3])
y = np.array([10, 20, 30, 40])
plt.subplot(2, 1, 2)
plt.plot(x, y)
plt.title("Subplot 2 (Bottom)")

plt.tight_layout()
plt.show()
""")

    # Data Setup for Pandas Plotting (Sections 10 to 16)
    add_md("""---
### 🛠️ Sample Dataset Setup for Pandas Plotting (Sections 10–16)
The following code creates the standard 4-column DataFrame (`df`) with columns `['col_1', 'col_2', 'col_3', 'col_4']` used throughout the Pandas visualization examples.
""")
    add_code("""import numpy as np
import pandas as pd
import matplotlib.pyplot as plt

# Generate a consistent sample DataFrame for plotting demonstrations
np.random.seed(42)
df = pd.DataFrame(
    np.random.randn(10, 4),
    index=pd.date_range('2023-01-01', periods=10),
    columns=['col_1', 'col_2', 'col_3', 'col_4']
).cumsum()

print("Sample DataFrame for Pandas Plotting:")
print(df.head())
""")

    # 10. Pandas Line Plot
    add_md("""<a id="u5_10"></a>
### 10. Pandas Line Plot
Demonstrates:
- Standard multi-column line plot: `df.plot()`
- Line plot with specific X and Y columns: `df.plot(x="col_1", y="col_2")`
- Separate subplots per column: `df.plot(subplots=True, figsize=(8, 8))`
""")
    add_code("""# 1. Standard multi-column line plot
df.plot(title="Standard Line Plot")
plt.show()

# 2. Line plot specifying X and Y columns
df.plot(x="col_1", y="col_2", title="col_1 vs col_2 Line Plot")
plt.show()

# 3. Separate subplots for each column
df.plot(subplots=True, figsize=(8, 8))
plt.tight_layout()
plt.show()
""")

    # 11. Pandas Bar Plots
    add_md("""<a id="u5_11"></a>
### 11. Pandas Bar Plots
Demonstrates:
- Standard vertical bar plot: `df.plot(kind="bar")`
- Stacked vertical bar plot: `df.plot.bar(stacked=True)`
- Stacked horizontal bar plot: `df.plot.barh(stacked=True)`
""")
    add_code("""# 1. Standard vertical bar plot
df.plot(kind="bar", title="Vertical Bar Plot")
plt.show()

# 2. Stacked vertical bar plot
df.plot.bar(stacked=True, title="Stacked Vertical Bar Plot")
plt.show()

# 3. Stacked horizontal bar plot
df.plot.barh(stacked=True, title="Stacked Horizontal Bar Plot")
plt.show()
""")

    # 12. Pandas Box Plots
    add_md("""<a id="u5_12"></a>
### 12. Pandas Box Plots
Demonstrates:
- Standard box plot: `df.plot.box()`
- Alternative syntax with grid: `df.boxplot()`
- Horizontal box plot with custom positions: `df.plot.box(vert=False, positions=[1, 2, 3, 4])`
""")
    add_code("""# Standard box plot
df.plot.box(title="Standard Box Plot")
plt.show()

# Alternative syntax with grid
df.boxplot()
plt.title("Boxplot with Grid")
plt.show()

# Horizontal box plot with custom positions
df.plot.box(vert=False, positions=[1, 2, 3, 4], title="Horizontal Box Plot")
plt.show()
""")

    # 13. Pandas Area Plots
    add_md("""<a id="u5_13"></a>
### 13. Pandas Area Plots
Demonstrates:
- Stacked Area Plot: `df.plot.area()`
- Unstacked Area Plot: `df.plot.area(stacked=False)`
*(Note: Area plots require non-negative values, so `df.abs()` is used for visual demonstration).*
""")
    add_code("""# Note: Area plot requires all positive values, so we use df.abs()
df_pos = df.abs()

# 1. Stacked Area Plot
df_pos.plot.area(title="Stacked Area Plot")
plt.show()

# 2. Unstacked Area Plot
df_pos.plot.area(stacked=False, title="Unstacked Area Plot")
plt.show()
""")

    # 14. Pandas Scatter Plots
    add_md("""<a id="u5_14"></a>
### 14. Pandas Scatter Plots
Demonstrates:
- Simple Scatter Plot: `df.plot.scatter(x='col_1', y='col_3')`
- Overlaying Multiple Scatter Plots using `ax`
- Scatter Plot with Color Mapping (`c` parameter)
""")
    add_code("""# 1. Simple Scatter Plot
df.plot.scatter(x='col_1', y='col_3', title="Simple Scatter Plot")
plt.show()

# 2. Overlaying Multiple Scatter Plots
ax = df.plot.scatter(x="col_1", y="col_3", color="red", marker="*", s=100, label="col_1 vs col_3")
df.plot.scatter(x="col_2", y="col_4", color="orange", s=100, ax=ax, label="col_2 vs col_4")
plt.title("Overlaying Multiple Scatter Plots")
plt.legend()
plt.show()

# 3. Scatter Plot with Color Mapping (c parameter)
df.plot.scatter(x="col_2", y="col_4", c='col_1', s=100, colormap='viridis', title="Color-Mapped Scatter Plot")
plt.show()
""")

    # 15. Pie Chart
    add_md("""<a id="u5_15"></a>
### 15. Pie Chart
Demonstrates:
- Series Pie Chart
- DataFrame Pie Plot with subplots across columns
**Expected Output for Series:**
```
0    0.067722
1    0.875775
2    0.239062
3    0.860075
4    0.595066
dtype: float64
```
""")
    add_code("""import numpy as np
import pandas as pd
import matplotlib.pyplot as plt

# 1. Series Pie Chart
pie = pd.Series(np.random.rand(5))
print("Series values:")
print(pie)

pie.plot.pie(figsize=(6, 6), autopct="%.2f%%", title="Series Pie Chart")
plt.show()

# 2. DataFrame Pie Plot across subplots
df2 = pd.DataFrame(
    np.random.rand(6, 3),
    columns=('col_1', 'col_2', 'col_3'),
    index=['A', 'B', 'C', 'D', 'E', 'F']
)
df2.plot.pie(subplots=True, figsize=(15, 6), autopct="%.1f%%")
plt.tight_layout()
plt.show()
""")

    # 16. Histograms
    add_md("""<a id="u5_16"></a>
### 16. Histograms
Demonstrates:
- Simple Histogram: `df.plot.hist()`
- Stacked Histogram with Bins: `df.plot.hist(stacked=True, bins=20)`
- Cumulative Horizontal Histogram: `df.plot.hist(orientation="horizontal", cumulative=True)`
- Separate Histogram per Column: `df.diff().hist()`
""")
    add_code("""# 1. Simple Histogram
df.plot.hist(title="Simple Histogram")
plt.show()

# 2. Stacked Histogram with Bins
df.plot.hist(stacked=True, bins=20, title="Stacked Histogram with Bins")
plt.show()

# 3. Cumulative Horizontal Histogram
df.plot.hist(orientation="horizontal", cumulative=True, title="Cumulative Horizontal Histogram")
plt.show()

# 4. Separate Histogram per Column
df.diff().hist(figsize=(8, 8))
plt.suptitle("Separate Histogram per Column", y=1.02)
plt.tight_layout()
plt.show()
""")

    notebook = {
        "nbformat": 4,
        "nbformat_minor": 0,
        "metadata": {
            "colab": {
                "provenance": [],
                "name": "Python_Data_Analysis_and_Visualization.ipynb"
            },
            "kernelspec": {
                "name": "python3",
                "display_name": "Python 3"
            },
            "language_info": {
                "name": "python"
            }
        },
        "cells": cells
    }

    output_path = r"d:\AOPD Project\Python_Data_Analysis_and_Visualization.ipynb"
    with open(output_path, "w", encoding="utf-8") as f:
        json.dump(notebook, f, indent=2)

    print(f"Notebook successfully generated at: {output_path}")
    print(f"Total cells: {len(cells)}")

create_colab_notebook()
