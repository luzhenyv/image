package com.todata.image.model;

import java.util.*;

/**
 * @ClassName MaskRCNNConfig
 * @Author Zhen Lu
 * @Date 2020/11/26 14:32
 * @Function
 */
public class MaskRCNNConfig {
    public String NAME; // Name the configurations.
    public Integer GPU_COUNT = 1; // NUMBER OF GPUs to use.
    public Integer IMAGES_PER_GPU = 1; // Number of images to train with on each GPU.
    public Integer STEPS_PER_EPOCH = 1000; // Number of training steps per epoch.
    public Integer VALIDATION_STEPS = 50; // Number of validation steps to run at the end of every training epoch.
    public String BACKBONE = "resnet101"; // Backbone network architecture. Supported values are: resnet50, resnet101.
    public List<Integer> COMPUTE_BACKBONE_SHAPE;
    public List<Integer> BACKBONE_STRIDES =
            new ArrayList<>(Arrays.asList(4, 8, 16, 32, 64)); // The strides of each layer of the FPN Pyramid.
    public Integer FPN_CLASSIF_FC_LAYERS_SIZE = 1024; // ize of the fully-connected layers in the classification graph
    public Integer TOP_DOWN_PYRAMID_SIZE = 256; // Size of the top-down layers used to build the feature pyramid
    public Integer NUM_CLASSES = 1; // Number of classification classes (including background)
    public List<Integer> RPN_ANCHOR_SCALES =
            new ArrayList<>(Arrays.asList(32, 64, 128, 256, 512)); // Length of square anchor side in pixels
    public List<Float> RPN_ANCHOR_RATIOS =
            new ArrayList<>(Arrays.asList(0.5f, 1f, 2f)); // Ratios of anchors at each cell (width/height)
    public Integer RPN_ANCHOR_STRIDE = 2; // Anchor stride
    public Float RPN_NMS_THRESHOLD = 0.7f; // Non-max suppression threshold to filter RPN proposals.
    public Integer RPN_TRAIN_ANCHORS_PER_IMAGE = 256; // How many anchors per image to use for RPN training
    public Integer PRE_NMS_LIMIT = 6000; // ROIs kept after tf.nn.top_k and before non-maximum suppression
    public Integer POST_NMS_ROIS_TRAINING = 2000; // ROIs kept after non-maximum suppression (training and inference)
    public Integer POST_NMS_ROIS_INFERENCE = 1000;
    public Boolean USE_MINI_MASK = true; // If enabled, resizes instance masks to a smaller size to reduce memory load. Recommended when using high-resolution images.
    public List<Integer> MINI_MASK_SHAPE =
            new ArrayList<>(Arrays.asList(56, 56));
    public String IMAGE_RESIZE_MODE = "square"; // Input image resizing, Supported values are: none, square, pad64, crop
    public Integer IMAGE_MIN_DIM = 800;
    public Integer IMAGE_MAX_DIM = 1024;
    public Boolean IMAGE_PADDING = true;
    public Float IMAGE_MIN_SCALE = 0f;
    public Integer IMAGE_CHANNEL_COUNT = 3;
    public List<Float> MEAN_PIXEL =
            new ArrayList<>(Arrays.asList(123.7f, 116.8f, 103.9f));
    public Integer TRAIN_ROIS_PER_IMAGE = 200;
    public Float ROI_POSITIVE_RATIO = 0.33f;
    public Integer POOL_SIZE = 7;
    public Integer MASK_POOL_SIZE = 14;
    public List<Integer> MASK_SHAPE =
            new ArrayList<>(Arrays.asList(28, 28));
    public Integer MAX_GT_INSTANCES = 100;
    public List<Float> RPN_BBOX_STD_DEV =
            new ArrayList<>(Arrays.asList(0.1f, 0.1f, 0.2f, 0.2f));
    public List<Float> BBOX_STD_DEV =
            new ArrayList<>(Arrays.asList(0.1f, 0.1f, 0.2f, 0.2f));
    public Integer DETECTION_MAX_INSTANCES = 100;
    public Float DETECTION_MIN_CONFIDENCE = 0.7f;
    public Float DETECTION_NMS_THRESHOLD = 0.3f;
    public Float LEARNING_RATE = 0.001f;
    public Float LEARNING_MOMENTUM = 0.9f;
    public Float WEIGHT_DECAY = 0.0001f;
    public Map<String, Float> LOSS_WEIGHTS = new HashMap<>();
    public Boolean USE_RPN_ROIS = true;
    public Boolean TRAIN_BN = false;
    public Float GRADIENT_CLIP_NORM = 5.0f;

    public List<Integer> IMAGE_SHAPE;
    public Integer BATCH_SIZE;
    public Integer IMAGE_META_SIZE;

    public MaskRCNNConfig() {
    }

    public void update() {
        BATCH_SIZE = IMAGES_PER_GPU * GPU_COUNT;

        if (IMAGE_RESIZE_MODE.equals("crop"))
            IMAGE_SHAPE = new ArrayList<>(Arrays.asList(IMAGE_MIN_DIM, IMAGE_MIN_DIM, IMAGE_CHANNEL_COUNT));
        else
            IMAGE_SHAPE = new ArrayList<>(Arrays.asList(IMAGE_MAX_DIM, IMAGE_MAX_DIM, IMAGE_CHANNEL_COUNT));

        IMAGE_META_SIZE = 1 + 3 + 3 + 4 + 1 + NUM_CLASSES;
    }

    @Override
    public String toString() {
        return "MaskRCNNConfig{" +
                "BACKBONE='" + BACKBONE + '\'' +
                ", BACKBONE_STRIDES=" + BACKBONE_STRIDES +
                ", BATCH_SIZE=" + BATCH_SIZE +
                ", BBOX_STD_DEV=" + BBOX_STD_DEV +
                ", COMPUTE_BACKBONE_SHAPE=" + COMPUTE_BACKBONE_SHAPE +
                ", DETECTION_MAX_INSTANCES=" + DETECTION_MAX_INSTANCES +
                ", DETECTION_MIN_CONFIDENCE=" + DETECTION_MIN_CONFIDENCE +
                ", DETECTION_NMS_THRESHOLD=" + DETECTION_NMS_THRESHOLD +
                ", FPN_CLASSIF_FC_LAYERS_SIZE=" + FPN_CLASSIF_FC_LAYERS_SIZE +
                ", GPU_COUNT=" + GPU_COUNT +
                ", GPU_COUNT=" + GPU_COUNT +
                ", GRADIENT_CLIP_NORM=" + GRADIENT_CLIP_NORM +
                ", IMAGES_PER_GPU=" + IMAGES_PER_GPU +
                ", IMAGE_CHANNEL_COUNT=" + IMAGE_CHANNEL_COUNT +
                ", IMAGE_MAX_DIM=" + IMAGE_MAX_DIM +
                ", IMAGE_META_SIZE=" + IMAGE_META_SIZE +
                ", IMAGE_MIN_DIM=" + IMAGE_MIN_DIM +
                ", IMAGE_MIN_SCALE=" + IMAGE_MIN_SCALE +
                ", IMAGE_PADDING=" + IMAGE_PADDING +
                ", IMAGE_RESIZE_MODE='" + IMAGE_RESIZE_MODE + '\'' +
                ", IMAGE_SHAPE=" + IMAGE_SHAPE +
                ", LEARNING_MOMENTUM=" + LEARNING_MOMENTUM +
                ", LEARNING_RATE=" + LEARNING_RATE +
                ", LOSS_WEIGHTS=" + LOSS_WEIGHTS +
                ", MASK_POOL_SIZE=" + MASK_POOL_SIZE +
                ", MASK_SHAPE=" + MASK_SHAPE +
                ", MAX_GT_INSTANCES=" + MAX_GT_INSTANCES +
                ", MEAN_PIXEL=" + MEAN_PIXEL +
                ", MINI_MASK_SHAPE=" + MINI_MASK_SHAPE +
                ", NAME='" + NAME + '\'' +
                ", NUM_CLASSES=" + NUM_CLASSES +
                ", POOL_SIZE=" + POOL_SIZE +
                ", POST_NMS_ROIS_INFERENCE=" + POST_NMS_ROIS_INFERENCE +
                ", POST_NMS_ROIS_TRAINING=" + POST_NMS_ROIS_TRAINING +
                ", PRE_NMS_LIMIT=" + PRE_NMS_LIMIT +
                ", ROI_POSITIVE_RATIO=" + ROI_POSITIVE_RATIO +
                ", RPN_ANCHOR_RATIOS=" + RPN_ANCHOR_RATIOS +
                ", RPN_ANCHOR_SCALES=" + RPN_ANCHOR_SCALES +
                ", RPN_ANCHOR_STRIDE=" + RPN_ANCHOR_STRIDE +
                ", RPN_BBOX_STD_DEV=" + RPN_BBOX_STD_DEV +
                ", RPN_NMS_THRESHOLD=" + RPN_NMS_THRESHOLD +
                ", RPN_TRAIN_ANCHORS_PER_IMAGE=" + RPN_TRAIN_ANCHORS_PER_IMAGE +
                ", STEPS_PER_EPOCH=" + STEPS_PER_EPOCH +
                ", TOP_DOWN_PYRAMID_SIZE=" + TOP_DOWN_PYRAMID_SIZE +
                ", TRAIN_BN=" + TRAIN_BN +
                ", TRAIN_ROIS_PER_IMAGE=" + TRAIN_ROIS_PER_IMAGE +
                ", VALIDATION_STEPS=" + VALIDATION_STEPS +
                ", USE_MINI_MASK=" + USE_MINI_MASK +
                ", USE_RPN_ROIS=" + USE_RPN_ROIS +
                ", WEIGHT_DECAY=" + WEIGHT_DECAY +
                '}';
    }
}
