package ca.fraggergames.ffxivextract.gui.components;

import java.awt.BorderLayout;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.Scanner;

import javax.media.opengl.GL;
import javax.media.opengl.GL3;
import javax.media.opengl.GL3bc;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.GLProfile;
import javax.media.opengl.awt.GLCanvas;
import javax.media.opengl.glu.GLU;
import javax.swing.BoxLayout;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;

import ca.fraggergames.ffxivextract.helpers.Matrix;
import ca.fraggergames.ffxivextract.helpers.ImageDecoding.ImageDecodingException;
import ca.fraggergames.ffxivextract.models.HairShader;
import ca.fraggergames.ffxivextract.models.IrisShader;
import ca.fraggergames.ffxivextract.models.Material;
import ca.fraggergames.ffxivextract.models.Mesh;
import ca.fraggergames.ffxivextract.models.Model;
import ca.fraggergames.ffxivextract.models.Shader;
import ca.fraggergames.ffxivextract.models.Texture_File;

import com.jogamp.common.nio.Buffers;
import com.jogamp.opengl.util.Animator;
import java.awt.FlowLayout;
import javax.swing.DefaultComboBoxModel;

public class OpenGL_View extends JPanel {

	//UI
	JLabel lblVertices, lblIndices, lblMeshes;
	JComboBox cmbLodChooser, cmbVariantChooser;
	
	Animator animator;
	ModelRenderer renderer;
	JLabel lbl1;
	
	private boolean leftMouseDown = false;
	private boolean rightMouseDown = false;
	
	private int currentLoD = 0;
	private int lastOriginX, lastOriginY;
	private int lastX, lastY;		
	
	public OpenGL_View(final Model model) {
		GLProfile glProfile = GLProfile.getDefault();
		GLCapabilities glcapabilities = new GLCapabilities( glProfile );
        final GLCanvas glcanvas = new GLCanvas( glcapabilities );
        renderer = new ModelRenderer(model);
        glcanvas.addGLEventListener(renderer);
        animator = new Animator(glcanvas);
        animator.start();
        glcanvas.addMouseMotionListener(new MouseMotionListener() {
			
			@Override
			public void mouseMoved(MouseEvent e) {
				
			}
			
			@Override
			public void mouseDragged(MouseEvent e) {
				if (leftMouseDown)
				{					
					renderer.pan((e.getX() - lastX), (e.getY() - lastY));
					lastX = e.getX();
					lastY = e.getY();
				}
				if (rightMouseDown)
				{
					renderer.rotate(e.getX() - lastX, e.getY() - lastY);
					lastX = e.getX();
					lastY = e.getY();
				}
			}
		});
        glcanvas.addMouseListener(new MouseListener() {
			
			@Override
			public void mouseReleased(MouseEvent e) {
				if (e.getButton() == MouseEvent.BUTTON1)				
					leftMouseDown = false;				
				if (e.getButton() == MouseEvent.BUTTON3)
					rightMouseDown = false;
			}
			
			@Override
			public void mousePressed(MouseEvent e) {
				if (e.getButton() == MouseEvent.BUTTON1)	
				{
					leftMouseDown = true;
					lastOriginX = e.getX();
					lastOriginY = e.getY();
					lastX = lastOriginX;
					lastY = lastOriginY;
				}
				if (e.getButton() == MouseEvent.BUTTON3)
				{
					rightMouseDown = true;
					lastOriginX = e.getX();
					lastOriginY = e.getY();
					lastX = lastOriginX;
					lastY = lastOriginY;
				}
			}
			
			@Override
			public void mouseExited(MouseEvent arg0) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void mouseEntered(MouseEvent arg0) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void mouseClicked(MouseEvent arg0) {
				// TODO Auto-generated method stub
				
			}
		});
        addMouseWheelListener(new MouseWheelListener() {
			
			@Override
			public void mouseWheelMoved(MouseWheelEvent e) {
				int notches = e.getWheelRotation();		
				renderer.zoom(-notches);				
			}
		});
        setLayout(new BorderLayout(0, 0));
        
        JPanel panel = new JPanel();
        panel.setBorder(new TitledBorder(null, "Model Info", TitledBorder.LEADING, TitledBorder.TOP, null, null));
        add(panel, BorderLayout.NORTH);
        panel.setLayout(new BorderLayout(0, 0));
        
        JPanel panel_1 = new JPanel();
        panel.add(panel_1, BorderLayout.EAST);
        panel_1.setLayout(new BoxLayout(panel_1, BoxLayout.Y_AXIS));
        
        JPanel panel_3 = new JPanel();
        FlowLayout flowLayout_1 = (FlowLayout) panel_3.getLayout();
        flowLayout_1.setAlignment(FlowLayout.RIGHT);
        panel_1.add(panel_3);
        
        lbl1 = new JLabel("Detail Level:");
        panel_3.add(lbl1);
        
        cmbLodChooser = new JComboBox();
        panel_3.add(cmbLodChooser);
        cmbLodChooser.addItem("0");
        cmbLodChooser.addItem("1");
        cmbLodChooser.addItem("2");
        cmbLodChooser.setLightWeightPopupEnabled(false);
        
        cmbLodChooser.addItemListener(new ItemListener() {
			
			@Override
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == ItemEvent.SELECTED) {
			          currentLoD = Integer.parseInt((String)e.getItem());
			    }
			}
		});
        
        JPanel panel_4 = new JPanel();
        FlowLayout flowLayout = (FlowLayout) panel_4.getLayout();
        flowLayout.setAlignment(FlowLayout.RIGHT);
        panel_1.add(panel_4);
        
        JLabel lblVariant = new JLabel("Variant:");
        panel_4.add(lblVariant);
        
        cmbVariantChooser = new JComboBox();
        cmbVariantChooser.setLightWeightPopupEnabled(false);
        
        int variantChooserModel[] = new int[model.getNumVariants() == -1 ? 0 : model.getNumVariants()];
        for (int i = 0; i < variantChooserModel.length; i++)
        	cmbVariantChooser.addItem("" + (i+1));
        
        cmbVariantChooser.addItemListener(new ItemListener() {
			
			@Override
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == ItemEvent.SELECTED) {
			          model.loadMaterials(Integer.parseInt((String)e.getItem()));
			          renderer.resetMaterial();
			    }
			}
		});
        
        panel_4.add(cmbVariantChooser);
        
        JPanel panel_5 = new JPanel();
        panel.add(panel_5, BorderLayout.CENTER);
        panel_5.setLayout(new BoxLayout(panel_5, BoxLayout.X_AXIS));
        
        
        JPanel panel_2 = new JPanel();
        panel_5.add(panel_2);
        panel_2.setLayout(new BoxLayout(panel_2, BoxLayout.Y_AXIS));
        
        lblVertices = new JLabel("Vertices:");
        panel_2.add(lblVertices);
        
        lblIndices = new JLabel("Indices:");
        panel_2.add(lblIndices);
        
        lblMeshes = new JLabel("Meshes:");
        panel_2.add(lblMeshes);
        
        add( glcanvas, BorderLayout.CENTER);
	}
	
	class ModelRenderer implements GLEventListener{

		private Model model;
		private GLU glu;
		private float zoom = -7;
		private float panX = 0;
		private float panY = 0;
		private float angleX = 0;
		private float angleY = 0;
		
		private int[] textureIds;		
		
		//Matrices
		float[] modelMatrix = new float[16];
		float[] viewMatrix = new float[16];
		float[] projMatrix = new float[16];
		
		public ModelRenderer(Model model) {
			this.model = model;
			textureIds = new int[model.getNumMaterials() * 4];
		}

		public void resetMaterial() {
			loaded = false;
		}

		public void zoom(int notches) {
			zoom += notches * 0.25f;
		}
		
		public void rotate(float x, float y)
		{
			angleX += x * 1.0f;
			angleY += y * 1.0f;
		}
		
		public void pan(float x, float y)
		{
			panX += x * 0.05f;
			panY += -y * 0.05f;
		}

		boolean loaded = false;
		
		
		/////DEFAULT COLORS//////
		float hairColor[] = {0.1f,0.1f,0.1f,1.0f};
		float highlightColor[] = {0.650f,0.502f,0.392f,1.0f};		
		float eyeColor[] = {0.0f,0.302f,0.0f,1.0f};
		/////DEFAULT COLORS//////
		
		@Override
		public void display(GLAutoDrawable drawable) {
			GL3bc gl = drawable.getGL().getGL3bc();
			
			if (!loaded)
			{
				for (int i = 0; i < model.getNumMaterials(); i++){
					
					if (model.getMaterial(i) == null)
						break;
					
					gl.glGenTextures(4, model.getMaterial(i).getGLTextureIds(),0);												
					Material m = model.getMaterial(i);
					
					for (int j = 0; j < 4; j++){
						
						Texture_File tex = null;
						
						switch(j)
						{
						case 0: 
							tex = m.getDiffuseMapTexture();
							break;
						case 1: 
							tex = m.getNormalMapTexture();
							break;
						case 2: 
							tex = m.getSpecularMapTexture();
							break;
						case 3: 
							tex = m.getColorSetTexture();
							break;
						}
						
						if (tex == null)
							continue;
						
						BufferedImage img = null;
						try {
							img = tex.decode(0, null);
						} catch (ImageDecodingException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}								
						
						int[] pixels = new int[img.getWidth() * img.getHeight()];
						img.getRGB(0, 0, img.getWidth(), img.getHeight(), pixels, 0, img.getWidth());				
						
						ByteBuffer buffer = Buffers.newDirectByteBuffer(img.getWidth() * img.getHeight() * 4);
						
						//Fucking Java Trash
						for(int y = 0; y < img.getHeight(); y++){
				            for(int x = 0; x < img.getWidth(); x++){
				                int pixel = pixels[y * img.getWidth() + x];
				                buffer.put((byte) ((pixel >> 16) & 0xFF));     
				                buffer.put((byte) ((pixel >> 8) & 0xFF));      
				                buffer.put((byte) (pixel & 0xFF));               
				                buffer.put((byte) ((pixel >> 24) & 0xFF));
				            }
				        }				
				        buffer.flip(); //FOR THE LOVE OF GOD DO NOT FORGET THIS
						buffer.position(0);
						
				        //Load into VRAM
				        gl.glBindTexture(GL3.GL_TEXTURE_2D, m.getGLTextureIds()[j]);
						gl.glTexParameteri(GL3.GL_TEXTURE_2D, GL3.GL_TEXTURE_WRAP_S, GL3.GL_REPEAT);
						gl.glTexParameteri(GL3.GL_TEXTURE_2D, GL3.GL_TEXTURE_WRAP_T, GL3.GL_REPEAT);
						gl.glTexParameteri(GL3.GL_TEXTURE_2D, GL3.GL_TEXTURE_MIN_FILTER, GL3.GL_LINEAR);
						gl.glTexParameteri(GL3.GL_TEXTURE_2D, GL3.GL_TEXTURE_MAG_FILTER, GL3.GL_LINEAR);
						
						gl.glTexImage2D(GL3.GL_TEXTURE_2D, 0, GL3.GL_RGBA, img.getWidth(), img.getHeight(), 0, GL3.GL_RGBA, GL3.GL_UNSIGNED_BYTE, buffer);						
						gl.glBindTexture(GL3.GL_TEXTURE_2D, 0);
						
						m.loadShader(gl);
					}					
				}
				loaded = true;
			}
			
		    gl.glClear(GL3.GL_COLOR_BUFFER_BIT | GL3.GL_DEPTH_BUFFER_BIT); 		  
		    
		    Matrix.setIdentityM(viewMatrix, 0);
		    Matrix.translateM(viewMatrix, 0, panX, panY, zoom);
		    Matrix.rotateM(viewMatrix, 0, angleX, 0, 1, 0);
		    Matrix.rotateM(viewMatrix, 0, angleY, 1, 0, 0);		     		   		    		    		    
		    
		    for (int i = 0; i < model.getNumMesh(currentLoD); i++){
		    	
		    	Mesh mesh = model.getMeshes(currentLoD)[i];
		    	Material material = model.getMaterial(mesh.materialNumber);
		    	Shader shader = material.getShader();
		    	
		    	gl.glUseProgram(shader.getShaderProgramID());
		    	
		    	mesh.vertBuffer.position(0);
		    	mesh.indexBuffer.position(0);
		    	
		    	//Position
		    	if (mesh.vertexSize == 0x10 || mesh.vertexSize == 0x8)
		    		gl.glVertexAttribPointer(shader.getAttribPosition(), 4, GL3.GL_HALF_FLOAT, false, 0, mesh.vertBuffer);
			    else if (mesh.vertexSize == 0x14)
			    	gl.glVertexAttribPointer(shader.getAttribPosition(), 3, GL3.GL_FLOAT, false, 0, mesh.vertBuffer);
		    	
		    	//Normal
		    	ByteBuffer normalData = mesh.vertBuffer.duplicate();			    
			    if (mesh.vertexSize == 0x10 || mesh.vertexSize == 0x8)
			    	normalData.position(mesh.numVerts*8);
			    else
			    	normalData.position(mesh.numVerts*12);		    	
		    	gl.glVertexAttribPointer(shader.getAttribNormal(), 4, GL3.GL_HALF_FLOAT, false, 24, normalData);
		    	
		    	//Tex Coord
		    	ByteBuffer texData = mesh.vertBuffer.duplicate();			    
			    if (mesh.vertexSize == 0x10 || mesh.vertexSize == 0x8)
			    	texData.position((mesh.numVerts*8) + 16);
			    else
			    	texData.position((mesh.numVerts*12)+ 16);		
		    	gl.glVertexAttribPointer(shader.getAttribTexCoord(), 4, GL3.GL_HALF_FLOAT, false, 24, texData);
		    	
		    	//BiNormal
		    	ByteBuffer binormalData = mesh.vertBuffer.duplicate();			    
			    if (mesh.vertexSize == 0x10 || mesh.vertexSize == 0x8)
			    	binormalData.position(mesh.numVerts*8+8);
			    else
			    	binormalData.position(mesh.numVerts*12+8);		    	
		    	gl.glVertexAttribPointer(shader.getAttribBiTangent(), 4, GL3.GL_UNSIGNED_BYTE, false, 24, binormalData);
		    	
		    	//Color
		    	ByteBuffer colorData = mesh.vertBuffer.duplicate();			    
			    if (mesh.vertexSize == 0x10 || mesh.vertexSize == 0x8)
			    	colorData.position((mesh.numVerts*8) + 12);
			    else
			    	colorData.position((mesh.numVerts*12)+ 12);	
		    	gl.glVertexAttribPointer(shader.getAttribColor(), 4, GL3.GL_UNSIGNED_BYTE, false, 24, colorData);
		    	
		    	shader.setTextures(gl, material);
		    	shader.setMatrix(gl, modelMatrix, viewMatrix, projMatrix);
			    	
		    	if (shader instanceof HairShader)
		    		((HairShader)shader).setHairColor(gl, hairColor, highlightColor);
		    	else if (shader instanceof IrisShader)
		    		((IrisShader)shader).setEyeColor(gl, eyeColor);		    	
		    	
		    	//Draw
		    	shader.enableAttribs(gl);
			    gl.glDrawElements(GL3.GL_TRIANGLES, mesh.numIndex, GL3.GL_UNSIGNED_SHORT, mesh.indexBuffer);			    
			    shader.disableAttribs(gl);			  
			    
			}
		}

		@Override
		public void dispose(GLAutoDrawable drawable) {
		}

		@Override
		public void init(GLAutoDrawable drawable) {
			GL3 gl = drawable.getGL().getGL3();      // get the OpenGL graphics context
		      glu = new GLU();                         // get GL Utilities
		      gl.glClearColor(0.3f, 0.3f, 0.3f, 0.0f); // set background (clear) color
		      gl.glClearDepth(1.0f);      // set clear depth value to farthest
		      gl.glEnable(GL3.GL_DEPTH_TEST); // enables depth testing
		      gl.glDepthFunc(GL3.GL_LEQUAL);  // the type of depth test to do
		      gl.glEnable(GL3.GL_BLEND); 
		      gl.glBlendFunc (GL3.GL_SRC_ALPHA, GL3.GL_ONE_MINUS_SRC_ALPHA);		      
		      gl.glEnable(GL3.GL_TEXTURE_2D);
		    		      
		      
		}
		
		@Override
		public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height){
			GL3bc gl = drawable.getGL().getGL3bc();  // get the OpenGL 2 graphics context
			 
		      if (height == 0) height = 1;   // prevent divide by zero
		      float aspect = (float)width / height;
		 
		      gl.glViewport(0, 0, width, height);
		 
		      Matrix.setIdentityM(projMatrix, 0);
		      Matrix.perspectiveM(projMatrix, 0, 45.0f, aspect, 0.1f, 100.0f);
		     
		      Matrix.setIdentityM(modelMatrix, 0);
		      Matrix.setIdentityM(viewMatrix, 0);
		}
		
	}
	
}
