# AssignmentView

AssignmentView is an Android ListView widget which scrolls in a horizontal manner (in contrast with the SDK-provided ListView which scrolls vertically).

## Usage
To use in an XML layout:
 0. Include The Library into your project
 0. Make sure you are running ADT version 21 or greater
 0. Create the HorizontalListView as `com.okmobile.assignmentview.view.AssignmentView`

**Example**:

    <com.okmobile.assignmentview.view.AssignmentView
        android:id="@+id/HorizontalListView"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"/>

## Known Issues
 - Only the design is extracted with the resource file we have created.
 - Even if the height value is entered for the AssignmentView, it cannot be adjusted other than the value entered in the class.

## Contributors

 - [Ogün Can KAYA](https://github.com/oguncan)

## Licenses

This library licensed under the MIT license. This library makes use of code originally developed and licensed by [Ogün Can KAYA](oguncanlnx@gmail.com).

    The MIT License Copyright (c) 2022 Ogün Can KAYA (oguncanlnx@gmail.com)
    
    Permission is hereby granted, free of charge, to any person obtaining a copy of this software and
    associated documentation files (the "Software"), to deal in the Software without restriction,
    including without limitation the rights to use, copy, modify, merge, publish, distribute,
    sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is
    furnished to do so, subject to the following conditions:
    
    The above copyright notice and this permission notice shall be included in all copies or
    substantial portions of the Software.
    
    THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT
    NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
    NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
    DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
    OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.