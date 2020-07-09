package me.shetj.base;

/**
 * <b>@author：</b> shetj<br>
 * <b>@createTime：</b> 2020/6/16 0016<br>
 * <b>@email：</b> 375105540@qq.com<br>
 * <b>@describe</b>  <br>
 */
class LeetCode {

  public class TreeNode {
    int val;
    TreeNode left;
    TreeNode right;

    TreeNode(int x) {
      val = x;
    }
  }

  public class Codec {

    // Encodes a tree to a single string.
    public String serialize(TreeNode root) {


      return "[" + indexNode(null, root) + "]";
    }

    private String indexNode(String s, TreeNode root) {
      while (root == null) {
        String s1 = "";
        if (!s.isEmpty()) {
          s1 = s + "," + root.val;
        }
        return indexNode(indexNode(s1, root.left), root.right);
      }
      return "null";
    }

    // Decodes your encoded data to tree.
    public TreeNode deserialize(String data) {
      String substring = data.substring(1, data.length() - 1);
      String[] strings = substring.split(",");
      return insetNode(0, strings);
    }

    private TreeNode insetNode(int position, String[] strings) {
      TreeNode treeNodex = null;
      if (!strings[position].isEmpty()) {
        treeNodex = new TreeNode(Integer.parseInt(strings[position]));
      }
      TreeNode treeNode = null;
      if (position == 0) {
        treeNode = treeNodex;
      } else {
        if (!strings[position].isEmpty()) {
          if (treeNode.left == null) {
            treeNode.left =  indexNode(position+1,strings);
          } else {
            treeNode.right = treeNodex;
          }
        }
      }



      return treeNode;
    }
  }


}
