#include <iostream>
#include <vector>

using namespace std;

// def bresenham(cord):
//     if len(cord) % 2 != 0:
//         raise ValueError("Invalid number of inputs")
//     retArray = []
//     for i in range(0, len(cord) - 2, 2):
//         # [x1, y1, x2, y2, x3, y3, x4, y4]
//         x1 = cord[i]
//         y1 = cord[i+1]
//         x2 = cord[i+2]
//         y2 = cord[i+3]
//         m = 2 * (y2 - y1)
//         m_error = m - (x2 - x1)
//         y = y1
//         for x in range(x1, x2+1):
//             if x > 0:
//                 if x > 254:
//                     x = 254
//                 if y > 254:
//                     y = 254
//                 retArray.append(x)
//                 retArray.append(y)
//             m_error = m_error + m
//             if m_error >= 0:
//                 y = y + 1
//                 m_error = m_error - (2 * (x2 - x1))
//     return retArray

int *bresenham(vector<int> arr) {
  int x1, x2, y1, y2, y, m, m_error, i, l;
  if ( (arr.size() % 2) != 0){
    return 0;
  }
  cout << arr.at(1) << endl;
  vector<int> retArray;
  for(i = 0; i < arr.size() - 2; i+= 2){
    x1 = arr.at(i);
    y1 = arr.at(i+1);
    x2 = arr.at(i+2);
    y2 = arr.at(i+3);
    m = 2 * (y2 - y1);
    m_error = m - (x2 - x1);
    y = y1;
    for (l = x1; l < x2+1; l++) {

    }
  }

  return 0;
}

int main() {
  vector<int> arr;
  arr.push_back(1);
  arr.push_back(2);
  bresenham(arr);
  return 0;
}
